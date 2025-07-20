package com.college.PlacementApl.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PlacementRecordResponseDto {
    private Long recordId;

    private Long studentId;
    private String studentName;
    private String rollNumber;

    private Long companyId;
    private String companyName;

    private Long visitId;
    private LocalDate visitDate;

    private String position;
    private String salaryPackage;
    private LocalDate placementDate;
    private boolean internship;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

