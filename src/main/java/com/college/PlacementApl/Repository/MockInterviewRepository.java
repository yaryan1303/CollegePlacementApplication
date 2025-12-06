package com.college.PlacementApl.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.college.PlacementApl.Model.MockInterview;

public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    List<MockInterview> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<MockInterview> findByStatus(MockInterview.InterviewStatus status);
    
    @Query("SELECT mi FROM MockInterview mi WHERE mi.studentId = :studentId AND mi.status = 'IN_PROGRESS'")
    Optional<MockInterview> findActiveInterviewByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT mi FROM MockInterview mi WHERE mi.technology = :technology ORDER BY mi.createdAt DESC")
    List<MockInterview> findByTechnology(@Param("technology") String technology);
}
