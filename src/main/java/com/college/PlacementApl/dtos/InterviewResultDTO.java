package com.college.PlacementApl.dtos;

import java.util.List;

import lombok.Data;

@Data
public class InterviewResultDTO {
    private Long interviewId;
    private Double overallScore;
    private String overallFeedback;
    private List<QuestionResultDTO> questionResults;
    private String strengths;
    private String improvements;
    private Integer totalQuestions;
    private Integer questionsAnswered;
}
