package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class AIAnalysisResponse {
    private String feedback;
    private Double clarityScore;
    private Double correctnessScore;
    private Double confidenceScore;
    private Double overallScore;
    private String suggestedFollowUp;
    private Boolean requiresFollowUp;
}
