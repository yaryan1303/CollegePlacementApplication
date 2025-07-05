package com.college.PlacementApl.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Model.StudentApplication;
import com.college.PlacementApl.Model.StudentDetails;


public interface ApplicationRepository extends JpaRepository<StudentApplication, Long> {
    boolean existsByStudentAndVisit(StudentDetails student, CompanyVisit visit);


}
