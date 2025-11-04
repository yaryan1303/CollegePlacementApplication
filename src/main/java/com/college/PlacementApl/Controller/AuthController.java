package com.college.PlacementApl.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.college.PlacementApl.Model.Role;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Repository.RoleRepository;
import com.college.PlacementApl.Repository.UserRepository;
import com.college.PlacementApl.Service.EmailService;
import com.college.PlacementApl.Service.SmsService;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.dtos.ForgotPasswordRequest;
import com.college.PlacementApl.dtos.GenericResponse;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.RegisterRequest;
import com.college.PlacementApl.dtos.ResetPasswordRequest;
import com.college.PlacementApl.dtos.UserResponseDto;
import com.college.PlacementApl.utilites.UserAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;

    private RoleRepository roleRepository;

    private EmailService emailService;

    private SmsService smsService;

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;


    @Value("${Fronted_url}")
    private String fronted_url;

    @Autowired
    public AuthController(UserService userService, RoleRepository roleRepository, EmailService emailService,
            SmsService smsService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/public/login")
    public ResponseEntity<?> LoginUser(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(userService.loginUser(loginRequest));

    }

    @PostMapping("/public/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Check if username exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken: " + registerRequest.getUsername());
        }

        // Check if email exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use: " + registerRequest.getEmail());
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        String requestedRole = registerRequest.getRole();
        String roleToAssign = (requestedRole == null || requestedRole.isBlank()) ? "ROLE_USER" : requestedRole;

        Role role = roleRepository.findByName(roleToAssign)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleToAssign));

        user.setRole(role);

        userService.registerUser(user);

        return convertToUserResponseDto(user);
    }

    public ResponseEntity<UserResponseDto> convertToUserResponseDto(User user) {

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getRole().getName());

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<GenericResponse> processForgotPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest,
            HttpServletRequest request) {

        String email = forgotPasswordRequest.getEmail();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse(false, "User not found"));
        }

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        userService.updateUserResetToken(email, resetToken);

        // Generate reset URL
        // String url = fronted_url + "/api/auth/public/reset-password?token=" + resetToken;
        String url = fronted_url + "/reset-password?token=" + resetToken;


        // Send via both channels
        boolean emailSent = false;
        boolean smsSent = false;
        List<String> errors = new ArrayList<>();

        // 1. Send Email
        try {
            emailSent = emailService.sendMail(url,
                    email);
        } catch (Exception e) {
            errors.add("Email failed: " + e.getMessage());
        }

        StudentDetails studentDetails = user.getStudentDetails();

        // 2. Send SMS if phone exists
        if (studentDetails != null && studentDetails.getPhoneNumber() != null
                && !studentDetails.getPhoneNumber().isBlank()) {
            try {
                String smsMessage = "Your password reset link: " + url;
                smsService.sendSMS(studentDetails.getPhoneNumber(), smsMessage);
                smsSent = true;
            } catch (Exception e) {
                errors.add("SMS failed: " + e.getMessage());
            }
        }

        // Response handling
        if (emailSent || smsSent) {
            String successMsg = "Reset instructions sent to: " +
                    (emailSent ? "email" : "") +
                    (emailSent && smsSent ? " and " : "") +
                    (smsSent ? "phone" : "");

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, successMsg));
        } else {
            return ResponseEntity.internalServerError()
                    .body(new GenericResponse(false,
                            "Failed to send instructions. " + String.join(", ", errors)));
        }
    }

    @GetMapping("/public/validate-reset-token")
    public ResponseEntity<GenericResponse> validateResetToken(@RequestParam String token) {
        User user = userService.getUserByToken(token);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse(false, "Invalid or expired reset token"));
        }

        // Return the email associated with the token for verification
        return ResponseEntity.ok()
                .body(new GenericResponse(true, token));
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest) {

        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();

        // Validate token first
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse(false, "Invalid or expired reset token"));
        }

        // Update password and clear token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Invalidate token after use
        userService.updateUser(user);

        return ResponseEntity.ok()
                .body(new GenericResponse(true, "Password reset successfully"));
    }

}
