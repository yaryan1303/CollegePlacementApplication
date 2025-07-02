package com.college.PlacementApl.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.User;
import com.college.PlacementApl.Repository.UserRepository;
import com.college.PlacementApl.Security.JwtAuthenticationResponse;
import com.college.PlacementApl.Security.JwtUtils;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.dtos.LoginRequest;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

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

}
