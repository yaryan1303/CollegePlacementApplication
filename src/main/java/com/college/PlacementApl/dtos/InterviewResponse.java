package com.college.PlacementApl.dtos;

import java.util.List;

import lombok.Data;

@Data
public class InterviewResponse {
    private String technology;
    private List<InterviewQA> questions;
    private String rawContent;
    
    public InterviewResponse(String technology, List<InterviewQA> questions, String rawContent) {
        this.technology = technology;
        this.questions = questions;
        this.rawContent = rawContent;
    }

}
