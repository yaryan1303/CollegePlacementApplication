package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class InterviewQuestionDTO {
    private Long questionId;
    private String question;
    private String difficulty;
    private String category;
    private Integer questionOrder;
    private Boolean isFollowUp;
}
