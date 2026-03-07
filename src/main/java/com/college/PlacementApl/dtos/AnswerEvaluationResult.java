package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerEvaluationResult {
    private Integer score;
    private String grade;
    private String feedback;
    private List<String> keyPointsCovered;
    private List<String> keyPointsMissed;
    private String improvedAnswer;
    private String error;
}
