package com.college.PlacementApl.dtos;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20)
    private String username;

    @Email
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String role;

}
