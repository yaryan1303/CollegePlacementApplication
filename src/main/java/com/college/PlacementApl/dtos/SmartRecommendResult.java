package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;

@Data
public class SmartRecommendResult {
    private List<SmartRecommendation> recommendations;
    private AtsScoreResult atsScore;
    private String aiFeedback;
    private ResumeProfile profile;
}
