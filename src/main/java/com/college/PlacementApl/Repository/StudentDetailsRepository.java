package com.college.PlacementApl.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.PlacementApl.Model.StudentDetails;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {

    Optional<StudentDetails> findByUserId(Long userId);

    // StudentDetails findByUserId(Long userId);

}
