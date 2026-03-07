package com.college.PlacementApl.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.college.PlacementApl.ResumeAnalyais.GroqResumeService;
import com.college.PlacementApl.Service.AiChatService;
import com.college.PlacementApl.dtos.AnswerEvaluationResult;
import com.college.PlacementApl.dtos.EvaluateAnswerRequest;
import com.college.PlacementApl.dtos.FollowupRequest;
import com.college.PlacementApl.dtos.GenerateRequest;
import com.college.PlacementApl.dtos.InterviewResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final GroqResumeService interviewService;
    private final AiChatService aiChatService;

    public InterviewController(GroqResumeService interviewService, AiChatService aiChatService) {
        this.interviewService = interviewService;
        this.aiChatService = aiChatService;
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

    // ==================== FEATURE 4: MOCK INTERVIEW EVALUATOR ====================
    // POST /api/interview/evaluate
    // Body: { "technology": "Java", "question": "What is JVM?", "studentAnswer": "JVM is..." }
    @PostMapping("/evaluate")
    public ResponseEntity<AnswerEvaluationResult> evaluateAnswer(
            @RequestBody EvaluateAnswerRequest request) {
        String raw = interviewService.evaluateInterviewAnswer(
            request.getTechnology(),
            request.getQuestion(),
            request.getStudentAnswer()
        ).join();
        AnswerEvaluationResult result = aiChatService.parseJson(
            raw, AnswerEvaluationResult.class, aiChatService.defaultEvaluation());
        return ResponseEntity.ok(result);
    }
}