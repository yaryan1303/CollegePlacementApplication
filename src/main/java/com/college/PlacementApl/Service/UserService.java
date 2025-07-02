package com.college.PlacementApl.Service;

import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.dtos.LoginRequest;

public interface UserService {

    public User registerUser(User user);

    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest);

}
