package com.college.PlacementApl.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.PlacementApl.Model.InterviewQuestion;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findByMockInterviewIdOrderByQuestionOrder(Long mockInterviewId);
    List<InterviewQuestion> findByMockInterviewIdAndIsFollowUp(Long mockInterviewId, Boolean isFollowUp);
    List<InterviewQuestion> findByMockInterviewIdAndParentQuestionId(Long mockInterviewId, Long parentQuestionId);
}
