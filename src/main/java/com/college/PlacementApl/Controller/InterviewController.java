package com.college.PlacementApl.Controller;

import org.springframework.web.bind.annotation.*;

import com.college.PlacementApl.ResumeAnalyais.GroqResumeService;
import com.college.PlacementApl.dtos.FollowupRequest;
import com.college.PlacementApl.dtos.GenerateRequest;
import com.college.PlacementApl.dtos.InterviewResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {
    
    private final GroqResumeService interviewService;
    
    public InterviewController(GroqResumeService interviewService) {
        this.interviewService = interviewService;
    }
    
    @PostMapping("/generate")
    public CompletableFuture<InterviewResponse> generateQuestions(@RequestBody GenerateRequest request) {
        return interviewService.generateInterviewQuestions(request.getTechnology());
    }
    
    @PostMapping("/followup")
    public CompletableFuture<String> askFollowup(@RequestBody FollowupRequest request) {
        return interviewService.askFollowupQuestion(
            request.getTechnology(), 
            request.getContext(), 
            request.getQuestion()
        );
    }
    
    @GetMapping("/technologies")
    public List<String> getPopularTechnologies() {
        return List.of(
            "Java", "Python", "JavaScript", "React", "Spring Boot", 
            "AWS", "Docker", "Kubernetes", "SQL", "MongoDB",
            "Node.js", "Angular", "Vue.js", "Machine Learning",
            "Data Structures", "Algorithms", "System Design"
        );
    }
}