package com.college.PlacementApl.Service;

import java.util.List;

import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.StudentDetailsDto;
import com.college.PlacementApl.dtos.StudentDetailsResponseDto;
import com.college.PlacementApl.dtos.StudentProfileDto;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    public User registerUser(User user);

    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest);

    public StudentDetailsResponseDto saveStudent(StudentDetailsDto studentDetailsDto);

    public StudentDetailsResponseDto getStudentDetails(Long userId);

    public Long getUserIdFromRequest(HttpServletRequest request);

    public StudentDetailsResponseDto updateStudentDetails(Long studentId, Long userId,
            StudentDetailsDto studentDetailsDto);

    public List<StudentProfileDto> getAllStudents();

    public StudentProfileDto getStudentById(Long id);

    User getUserByEmail(String email);

    User getUserByToken(String token);

    void updateUserResetToken(String email, String token);

    void updateUser(User user);

    public List<StudentDetailsResponseDto>getStudentByBatchYear(Integer batchYear);

    public List<StudentDetailsResponseDto>getStudentByDepartment(Long departmentId);

    public List<StudentDetailsResponseDto>getStudentByBatchYearAndDepartment(Integer batchYear,Long departmentId);

    StudentDetailsResponseDto updateStudentDetailsByUserId(Long userId, StudentDetailsDto studentDetailsDto);

}
