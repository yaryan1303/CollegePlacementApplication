package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class StudentDetailsResponseDto {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String rollNumber;
    private Integer batchYear;
    private String department;
    private Double cgpa;
    private String resumeUrl;
    private String phoneNumber;
    private String currentStatus;

}
