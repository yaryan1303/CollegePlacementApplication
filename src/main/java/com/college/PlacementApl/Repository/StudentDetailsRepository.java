package com.college.PlacementApl.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.college.PlacementApl.Model.Department;
import com.college.PlacementApl.Model.PlacementStatus;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.dtos.BatchStatsDto;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {

    Optional<StudentDetails> findByUserId(Long userId);

    // StudentDetails findByUserId(Long userId);

    @Query("SELECT new com.college.PlacementApl.dtos.BatchStatsDto(s.batchYear, COUNT(s), " +
            "SUM(CASE WHEN s.currentStatus = 'PLACED' THEN 1 ELSE 0 END), " +
            "(SUM(CASE WHEN s.currentStatus = 'PLACED' THEN 1 ELSE 0 END) * 100.0) / COUNT(s)) " +
            "FROM StudentDetails s GROUP BY s.batchYear")
    List<BatchStatsDto> findBatchWisePlacementStats();

    Long countByCurrentStatus(PlacementStatus status);

 // Find by batch year
    List<StudentDetails> findByBatchYear(Integer batchYear);

    // Find by department
    List<StudentDetails> findByDepartment(Department department);

    List<StudentDetails> findByBatchYearAndDepartment(Integer batchYear, Department department);

}
