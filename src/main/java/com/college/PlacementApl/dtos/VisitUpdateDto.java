package com.college.PlacementApl.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitUpdateDto {

    @NotNull(message = "Visit Date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate;
    
    @NotNull(message = "Application End Date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate application_end_date;
    
    @NotBlank(message = "Job Positions are required")
    private String jobPositions;
    
    @NotBlank(message = "Salary Package is required")
    private String salaryPackage;
    
    @NotBlank(message = "Eligibility Criteria is required")
    private String eligibilityCriteria;
}
