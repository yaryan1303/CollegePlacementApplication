package com.college.PlacementApl.dtos;

import lombok.Data;
import java.util.List;

@Data
public class StartInterviewRequest {
    private String technology;
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private Integer questionCount;
}
