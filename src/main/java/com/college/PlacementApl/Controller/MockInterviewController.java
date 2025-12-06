package com.college.PlacementApl.Controller;


import com.college.PlacementApl.Model.MockInterview;
import com.college.PlacementApl.Service.MockInterviewService;
import com.college.PlacementApl.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-interview")
@RequiredArgsConstructor
public class MockInterviewController {
    
    private final MockInterviewService mockInterviewService;
    
    @PostMapping("/start")
    public ResponseEntity<MockInterview> startInterview(
            @RequestBody StartInterviewRequest request,
            @RequestHeader("X-User-Id") Long studentId,
            @RequestHeader("X-User-Name") String studentName) {
        MockInterview interview = mockInterviewService.startInterview(request, studentId, studentName);
        return ResponseEntity.ok(interview);
    }
    
    @GetMapping("/{interviewId}/next-question")
    public ResponseEntity<List<InterviewQuestionDTO>> getNextQuestion(@PathVariable Long interviewId) {
        List<InterviewQuestionDTO> questions = mockInterviewService.getNextQuestion(interviewId);
        return ResponseEntity.ok(questions);
    }
    
    @PostMapping("/submit-answer")
    public ResponseEntity<AIAnalysisResponse> submitAnswer(@RequestBody AnswerSubmissionDTO submission) {
        AIAnalysisResponse response = mockInterviewService.submitAnswer(submission);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResultDTO> completeInterview(@PathVariable Long interviewId) {
        InterviewResultDTO result = mockInterviewService.completeInterview(interviewId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/my-interviews")
    public ResponseEntity<List<MockInterview>> getMyInterviews(@RequestHeader("X-User-Id") Long userId) {
        List<MockInterview> interviews = mockInterviewService.getStudentInterviews(userId);
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<MockInterview>> getAllInterviews() {
        List<MockInterview> interviews = mockInterviewService.getAllInterviews();
        return ResponseEntity.ok(interviews);
    }
    
    @GetMapping("/{interviewId}/result")
    public ResponseEntity<InterviewResultDTO> getInterviewResult(@PathVariable Long interviewId) {
        InterviewResultDTO result = mockInterviewService.completeInterview(interviewId);
        return ResponseEntity.ok(result);
    }
}
