package com.college.PlacementApl.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.college.PlacementApl.Model.Role;
import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Repository.RoleRepository;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.RegisterRequest;
import com.college.PlacementApl.dtos.UserResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;

    private RoleRepository roleRepository;

    @Autowired
    public AuthController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/public/login")
    public ResponseEntity<?> LoginUser(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(userService.loginUser(loginRequest));

    }

    @PostMapping("/public/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
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


}
