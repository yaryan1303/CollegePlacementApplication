package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;

@Data
public class ResumeAnalysisResult {
    private ResumeProfile profile;
    private List<Recommendation> recommendations;
    private String aiFeedback; // optional human-friendly feedback from HF
}
