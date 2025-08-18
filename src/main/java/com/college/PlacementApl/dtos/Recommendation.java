package com.college.PlacementApl.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Recommendation {
    private Long visitId;
    private String companyName;
    private String jobPositions;
    private String salaryPackage;
    private LocalDate visitDate;
    private LocalDate applicationDeadline;
    private String eligibilityCriteria;
     private Integer batchYear;
    private double score; // 0..1
    private String reason; // short reason why we matched
}
