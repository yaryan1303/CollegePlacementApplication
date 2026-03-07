package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;

@Data
public class PlacementPredictionRequest {
    private Double cgpa;
    private List<String> skills;
    private String department;
    private Integer batchYear;
}
