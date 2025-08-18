package com.college.PlacementApl.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementRecordDto {
    private Long recordId;
    private Long studentId;
    private String studentName;
    private Long companyId;
    private String companyName;
    private Long rollNumber;
    private Long visitId;
    private String position;
    private String salaryPackage;
    private LocalDate placementDate;
    private boolean isInternship;
}