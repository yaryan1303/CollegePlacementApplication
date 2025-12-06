package com.college.PlacementApl.Model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_interviews")
@Data
public class MockInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long studentId;
    
    @Column(nullable = false)
    private String studentName;
    
    @Column(nullable = false)
    private String technology;
    
    @Enumerated(EnumType.STRING)
    private InterviewStatus status;
    
    private Double overallScore;
    
    @Column(length = 2000)
    private String overallFeedback;
    
    private Integer totalQuestions;
    
    private Integer questionsAnswered;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "mockInterview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterviewQuestion> questions = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = InterviewStatus.IN_PROGRESS;
    }
    
    public enum InterviewStatus {
        IN_PROGRESS, COMPLETED, CANCELLED
    }
}
