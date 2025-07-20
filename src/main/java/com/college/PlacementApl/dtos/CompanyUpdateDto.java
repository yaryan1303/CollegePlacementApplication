package com.college.PlacementApl.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDto {

    @NotBlank(message = "Company name is required")
    @Size(max = 30, message = "Company name must be at most 30 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 55, message = "Description must be at most 55 characters")
    private String description;

    private String website;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact phone must be 10 digits")
    private String contactPhone;
}
