package com.college.PlacementApl.dtos;

import com.college.PlacementApl.Model.PlacementStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDto {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String rollNumber;
    private Integer batchYear;
    private String department;
    private Double cgpa;
    private String resumeUrl;
    private String phoneNumber;
    private PlacementStatus currentStatus;

}
