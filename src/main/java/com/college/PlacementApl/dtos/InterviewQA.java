package com.college.PlacementApl.dtos;

import lombok.Data;
import lombok.Getter;

@Data
public class InterviewQA {
    private String question;
    private String answer;
    private String difficulty;
    
    public InterviewQA(String question, String answer, String difficulty) {
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
    }

}
