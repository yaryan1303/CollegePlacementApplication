package com.college.PlacementApl.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyVisitResponseDto {
    private Long visitId;
    private LocalDate visitDate;
    private LocalDate applicationDeadline;
    private String jobPositions;
    private String salaryPackage;
    private String eligibilityCriteria;
    private Integer batchYear;

    private CompanyBasicDto company;
}

