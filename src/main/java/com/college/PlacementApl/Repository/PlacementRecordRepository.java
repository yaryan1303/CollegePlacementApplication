package com.college.PlacementApl.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.college.PlacementApl.Model.Company;
import com.college.PlacementApl.Model.PlacementRecord;
import com.college.PlacementApl.Model.StudentDetails;

public interface PlacementRecordRepository extends JpaRepository<PlacementRecord, Long> {

  boolean existsByStudentAndCompany(StudentDetails student, Company company);

  @Query("SELECT pr FROM PlacementRecord pr " +
      "JOIN FETCH pr.student s " +
      "JOIN FETCH pr.company c " +
      "JOIN FETCH pr.visit v " +
      "WHERE (:batchYear IS NULL OR s.batchYear = :batchYear) " +
      "AND (:companyName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :companyName, '%')))")
  List<PlacementRecord> findByBatchYearOrCompanyName(
      @Param("batchYear") Integer batchYear,
      @Param("companyName") String companyName);
}