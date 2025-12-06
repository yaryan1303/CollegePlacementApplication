package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class AnswerSubmissionDTO {
    private Long interviewId;
    private Long questionId;
    private String answer;
}