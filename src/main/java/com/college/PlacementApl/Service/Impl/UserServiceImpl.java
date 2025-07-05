package com.college.PlacementApl.Service.Impl;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.Repository.UserRepository;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.Security.JwtUtils;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.StudentDetailsDto;
import com.college.PlacementApl.dtos.StudentProfileDto;
import com.college.PlacementApl.utilites.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private JwtUtils jwtUtils;

    private StudentDetailsRepository studentDetailsRepository;

    
    private  ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            StudentDetailsRepository studentDetailsRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.studentDetailsRepository = studentDetailsRepository;
        this.modelMapper = modelMapper;
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);

    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Saving the Student
    // Information-------------------------------------------------------->>>>>>>

    @Override
    public StudentDetails saveStudent(StudentDetailsDto studentDetailsDto) {
        Optional<User> user = userRepository.findById(studentDetailsDto.getUserId());
        if (user.isPresent()) {

            StudentDetails studentDetails = new StudentDetails();

            studentDetails.setUser(user.get());
            studentDetails.setFirstName(studentDetailsDto.getFirstName());
            studentDetails.setLastName(studentDetailsDto.getLastName());
            studentDetails.setRollNumber(studentDetailsDto.getRollNumber());
            studentDetails.setBatchYear(studentDetailsDto.getBatchYear());
            studentDetails.setDepartment(studentDetailsDto.getDepartment());
            studentDetails.setCgpa(studentDetailsDto.getCgpa());
            studentDetails.setResumeUrl(studentDetailsDto.getResumeUrl());
            studentDetails.setPhoneNumber(studentDetailsDto.getPhoneNumber());
            studentDetails.setCurrentStatus(studentDetailsDto.getCurrentStatus());

            // Save the student details
            user.get().setStudentDetails(studentDetails);

            return studentDetailsRepository.save(studentDetails);
        }
        return null;

    }

    @Override
    public StudentDetails getStudentDetails(Long userId) {
        StudentDetails StudentbyUserId = studentDetailsRepository.findByUserId(userId).orElseThrow();
        return StudentbyUserId;

    }

    // Get UserId from Token

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtUtils.getJwtFromHeader(request);
        if (token != null && jwtUtils.validateToken(token)) {
            return jwtUtils.extractUserIdFromToken(token);
        }
        return null; // Or throw custom exception if needed
    }

    // Update studentDetails using studentId
    @Override
    public StudentDetails updateStudentDetails(Long studentId, Long userId, StudentDetailsDto studentDetailsDto) {
        StudentDetails student = studentDetailsRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to student record");
        }

        // Update student fields from DTO here
        student.setCgpa(studentDetailsDto.getCgpa());
        student.setDepartment(studentDetailsDto.getDepartment());
        student.setFirstName(studentDetailsDto.getFirstName());
        student.setLastName(studentDetailsDto.getLastName());
        student.setPhoneNumber(studentDetailsDto.getPhoneNumber());
        student.setResumeUrl(studentDetailsDto.getResumeUrl());
        student.setRollNumber(studentDetailsDto.getRollNumber());
        student.setCurrentStatus(studentDetailsDto.getCurrentStatus());

        return studentDetailsRepository.save(student);
    }

    // Fetch All the Students

    @Override
    public List<StudentProfileDto> getAllStudents() {
        return studentDetailsRepository.findAll().stream()
                .map(student -> modelMapper.map(student, StudentProfileDto.class))
                .toList();
    }

    @Override
     public StudentProfileDto getStudentById(Long id) {
        StudentDetails student = studentDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return modelMapper.map(student, StudentProfileDto.class);
    }

   

}
