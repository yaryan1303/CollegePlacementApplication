package com.college.PlacementApl.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Model.StudentApplication;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.utilites.ApplicationStatus;


public interface ApplicationRepository extends JpaRepository<StudentApplication, Long> {
    boolean existsByStudentAndVisit(StudentDetails student, CompanyVisit visit);
   List<StudentApplication> findByStudent_StudentId(Long studentId);
   List<StudentApplication> findByStatus(ApplicationStatus status);

   @Query("SELECT a FROM StudentApplication a " +
       "JOIN FETCH a.visit v " +
       "JOIN FETCH v.company c " +
       "WHERE c.name LIKE %:companyName%")
List<StudentApplication> findByCompanyNameContaining(@Param("companyName") String companyName);



    @Query("SELECT a FROM StudentApplication a " +
           "JOIN FETCH a.student " +
           "JOIN FETCH a.visit v " +
           "JOIN FETCH v.company " +
           "WHERE a.applicationId = :id")
    Optional<StudentApplication> findByIdWithDetails(@Param("id") Long id);



}
