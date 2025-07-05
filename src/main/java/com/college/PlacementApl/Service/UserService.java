package com.college.PlacementApl.Service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.LoginRequest;
import com.college.PlacementApl.dtos.StudentDetailsDto;
import com.college.PlacementApl.dtos.StudentProfileDto;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    public User registerUser(User user);

    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest);

    public StudentDetails saveStudent(StudentDetailsDto studentDetailsDto);

    public StudentDetails getStudentDetails(Long userId);

    public Long getUserIdFromRequest(HttpServletRequest request);

    public StudentDetails updateStudentDetails(Long studentId, Long userId, StudentDetailsDto studentDetailsDto);

    public  List<StudentProfileDto>  getAllStudents();

    public StudentProfileDto getStudentById(Long id);

    

}
