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

import com.college.PlacementApl.Model.Department;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Repository.DepartmentRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.Repository.UserRepository;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.Security.JwtUtils;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.StudentDetailsDto;
import com.college.PlacementApl.dtos.StudentDetailsResponseDto;
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

    private ModelMapper modelMapper;

    private DepartmentRepository departmentRepository;

    private StudentDetailsRepository studentRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            StudentDetailsRepository studentDetailsRepository, ModelMapper modelMapper,
            DepartmentRepository departmentRepository, StudentDetailsRepository studentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.studentDetailsRepository = studentDetailsRepository;
        this.modelMapper = modelMapper;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
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
    public StudentDetailsResponseDto saveStudent(StudentDetailsDto studentDetailsDto) {
        User user = userRepository.findById(studentDetailsDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + studentDetailsDto.getUserId()));

        Department department = departmentRepository.findById(studentDetailsDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with ID: " + studentDetailsDto.getDepartmentId()));

        if (studentDetailsRepository.findByRollNumber(studentDetailsDto.getRollNumber()).isPresent()) {
            throw new ResourceNotFoundException("Roll Number already exists");
        }

        StudentDetails studentDetails = new StudentDetails();
        studentDetails.setUser(user);
        studentDetails.setDepartment(department);
        studentDetails.setFirstName(studentDetailsDto.getFirstName());
        studentDetails.setLastName(studentDetailsDto.getLastName());
        studentDetails.setRollNumber(studentDetailsDto.getRollNumber());
        studentDetails.setBatchYear(studentDetailsDto.getBatchYear());
        studentDetails.setCgpa(studentDetailsDto.getCgpa());
        studentDetails.setResumeUrl(studentDetailsDto.getResumeUrl());
        studentDetails.setPhoneNumber(studentDetailsDto.getPhoneNumber());
        studentDetails.setCurrentStatus(studentDetailsDto.getCurrentStatus());

        // Establish bi-directional mapping if required
        user.setStudentDetails(studentDetails);

        studentDetailsRepository.save(studentDetails);

        return convertToStudentDetailsResponseDto(studentDetails);
    }

    public StudentDetailsResponseDto convertToStudentDetailsResponseDto(StudentDetails studentDetails) {
        StudentDetailsResponseDto responseDto = new StudentDetailsResponseDto();
        responseDto.setStudentId(studentDetails.getStudentId());
        responseDto.setFirstName(studentDetails.getFirstName());
        responseDto.setLastName(studentDetails.getLastName());
        responseDto.setRollNumber(studentDetails.getRollNumber());
        responseDto.setBatchYear(studentDetails.getBatchYear());
        responseDto.setDepartment(studentDetails.getDepartment().getName());
        responseDto.setCgpa(studentDetails.getCgpa());
        responseDto.setResumeUrl(studentDetails.getResumeUrl());
        responseDto.setPhoneNumber(studentDetails.getPhoneNumber());
        responseDto.setCurrentStatus(studentDetails.getCurrentStatus().toString());
        return responseDto;
    }

    @Override
    public StudentDetailsResponseDto getStudentDetails(Long userId) {
        StudentDetails StudentbyUserId = studentDetailsRepository.findByUserId(userId).orElseThrow();
        return convertToStudentDetailsResponseDto(StudentbyUserId);

    }

    // Get UserId from Token

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtUtils.getJwtFromHeader(request);
        if (token != null && jwtUtils.validateToken(token)) {
            return jwtUtils.extractUserIdFromToken(token);
        }
        return null;
    }

    // Update studentDetails using studentId
    @Override
    public StudentDetailsResponseDto updateStudentDetails(Long studentId, Long userId,
            StudentDetailsDto studentDetailsDto) {
        StudentDetails student = studentDetailsRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to student record");
        }
        Department department = departmentRepository.findById(studentDetailsDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        student.setDepartment(department);

        // Update student fields from DTO here
        student.setCgpa(studentDetailsDto.getCgpa());

        student.setFirstName(studentDetailsDto.getFirstName());
        student.setLastName(studentDetailsDto.getLastName());
        student.setPhoneNumber(studentDetailsDto.getPhoneNumber());
        student.setResumeUrl(studentDetailsDto.getResumeUrl());
        student.setRollNumber(studentDetailsDto.getRollNumber());
        student.setCurrentStatus(studentDetailsDto.getCurrentStatus());

        studentDetailsRepository.save(student);

        return convertToStudentDetailsResponseDto(student);
    }

    // Fetch All the Students

    // @Override
    // public List<StudentProfileDto> getAllStudents() {
    // return studentDetailsRepository.findAll().stream()
    // .map(student -> modelMapper.map(student, StudentProfileDto.class))
    // .toList();
    // }

    @Override
    public List<StudentProfileDto> getAllStudents() {
        return studentDetailsRepository.findAll().stream()
                .map(student -> new StudentProfileDto(
                        student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getRollNumber(),
                        student.getBatchYear(),
                        student.getDepartment() != null ? student.getDepartment().getName() : null, // assuming
                                                                                                    // Department has a
                                                                                                    // name
                        student.getCgpa(),
                        student.getResumeUrl(),
                        student.getPhoneNumber(),
                        student.getCurrentStatus()))
                .toList();
    }

    @Override
    public StudentProfileDto getStudentById(Long id) {
        StudentDetails student = studentDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return modelMapper.map(student, StudentProfileDto.class);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByResetToken(token)
                .orElse(null); // Returns null if token is invalid/expired
    }

    @Override
    public void updateUserResetToken(String email, String token) {
        User user = getUserByEmail(email);
        user.setResetToken(token);
        userRepository.save(user);
    }

    @Override
    public void updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new ResourceNotFoundException("User not found with id: " + user.getId());
        }
        userRepository.save(user);
    }

    // Fetch By BatchYear and Department

    public List<StudentDetailsResponseDto> getStudentByBatchYear(Integer batchYear) {
        List<StudentDetails> students = studentDetailsRepository.findByBatchYear(batchYear);
        return students.stream()
                .map(this::convertToStudentDetailsResponseDto)
                .toList();
    }

    public List<StudentDetailsResponseDto> getStudentByDepartment(Long departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            List<StudentDetails> students = studentDetailsRepository.findByDepartment(department.get());
            return students.stream()
                    .map(this::convertToStudentDetailsResponseDto)
                    .toList();
        } else {
            throw new ResourceNotFoundException("Department not found");
        }

    }

    public List<StudentDetailsResponseDto> getStudentByBatchYearAndDepartment(Integer batchYear, Long departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            List<StudentDetails> students = studentDetailsRepository.findByBatchYearAndDepartment(batchYear,
                    department.get());
            return students.stream()
                    .map(this::convertToStudentDetailsResponseDto)
                    .toList();
        } else {

            throw new ResourceNotFoundException("Department not found and Batch Year not found");
        }
    }

}
