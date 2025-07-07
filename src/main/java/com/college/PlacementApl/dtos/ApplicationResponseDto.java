package com.college.PlacementApl.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDto {

    private Long applicationId;
    private CompanyBasicDto company;
    private String jobPositions;
    private String salaryPackage;
    private String applicationStatus;
    private String feedback;
    private String applicationDate;
    private LocalDate visitDate;
    private LocalDate applicationDeadline;
    private String eligibilityCriteria;
     private Long studentId;
   private String firstName;
   private String lastName;
    private String rollNumber;
    private String department;

}
