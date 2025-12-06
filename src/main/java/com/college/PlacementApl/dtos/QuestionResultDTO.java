package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class QuestionResultDTO {
    private String question;
    private String userAnswer;
    private String feedback;
    private Double score;
    private String difficulty;
}
