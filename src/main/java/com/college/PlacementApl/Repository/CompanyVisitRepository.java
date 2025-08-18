package com.college.PlacementApl.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.PlacementApl.Model.CompanyVisit;

public interface CompanyVisitRepository  extends JpaRepository<CompanyVisit, Long> {

   List<CompanyVisit> findByIsActiveTrue();

   List<CompanyVisit> findByIsActiveTrueAndApplicationDeadlineAfter(LocalDate date);

}
