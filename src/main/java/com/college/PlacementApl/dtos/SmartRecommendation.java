package com.college.PlacementApl.dtos;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartRecommendation {
    private Long visitId;
    private String companyName;
    private String jobPositions;
    private String salaryPackage;
    private LocalDate visitDate;
    private LocalDate applicationDeadline;
    private String eligibilityCriteria;
    private Integer batchYear;

    private Integer matchScore;
    private String matchLevel;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private String recommendation;
}
