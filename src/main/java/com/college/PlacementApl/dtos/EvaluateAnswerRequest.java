package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class EvaluateAnswerRequest {
    private String technology;
    private String question;
    private String studentAnswer;
}
