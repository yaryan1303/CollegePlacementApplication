package com.college.PlacementApl.dtos;

import com.college.PlacementApl.Model.PlacementStatus;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailsDto {
    private Long userId;

    @NotBlank(message = "First name is required")
    @Size(max = 15, message = "First name must be at most 15 characters")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20, message = "Roll number must be at most 20 characters")
    private String rollNumber;

    private Integer batchYear;

    private Long departmentId;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "CGPA must be at most 10.0")
    private Double cgpa;

    private String resumeUrl;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private PlacementStatus currentStatus;
}
