package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementPredictionResult {
    private Integer probability;
    private String level;
    private String reasoning;
    private List<String> strengthAreas;
    private List<String> improvementAreas;
    private List<String> recommendedActions;
    private String error;
}
