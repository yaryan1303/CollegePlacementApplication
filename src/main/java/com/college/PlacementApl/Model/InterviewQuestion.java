package com.college.PlacementApl.Model;


import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "interview_questions")
@Data
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_interview_id")
    private MockInterview mockInterview;
    
    @Column(nullable = false, length = 1000)
    private String question;
    
      @Column(columnDefinition = "TEXT") // or use length = 4000
    private String aiFeedback;
    
    @Column(columnDefinition = "TEXT")
    private String userAnswer;
    
    private Double clarityScore;
    
    private Double correctnessScore;
    
    private Double confidenceScore;
    
    private Double overallScore;
    
    private String difficulty; // EASY, MEDIUM, HARD
    
    private String category; // THEORETICAL, PRACTICAL, BEHAVIORAL
    
    private Integer questionOrder;
    
    private Boolean isFollowUp = false;
    
    private Long parentQuestionId; // For follow-up questions
}
