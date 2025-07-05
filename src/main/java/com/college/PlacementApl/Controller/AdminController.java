package com.college.PlacementApl.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.college.PlacementApl.Service.AdminVisitService;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.Service.companyService;
import com.college.PlacementApl.dtos.CompanyCreateDto;
import com.college.PlacementApl.dtos.CompanyDto;
import com.college.PlacementApl.dtos.CompanyUpdateDto;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.StudentProfileDto;
import com.college.PlacementApl.dtos.VisitCreateDto;
import com.college.PlacementApl.dtos.VisitUpdateDto;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

   private UserService studentService;

   private companyService companyService;

   private  AdminVisitService visitService;

   @Autowired
    public AdminController(UserService studentService, companyService companyService, AdminVisitService visitService) {
        this.studentService = studentService;
        this.companyService = companyService;
        this.visitService = visitService;
    }

    ////////////////----------------------------------------------------------------->>>>>>>>
    /// Student Details

    // Fetch All the Students
    @GetMapping("/students")
    public ResponseEntity<List<StudentProfileDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentProfileDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }



    ////////////////////////////////--------------------------------------->>>>>>>>>>>>>>>>>>>>>>>
    /// Company Controller
    /// 
    
    @PostMapping("/companies")
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(createDto));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

   

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDto> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyUpdateDto updateDto) {
        return ResponseEntity.ok(companyService.updateCompany(id, updateDto));
    }

    @DeleteMapping("/companies/{id}")
    public String deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return "Company deleted successfully";
    }





    ///////////----------------------------------------------------------->>>>>>>>>
    /// Company Visit
    
    

    @PostMapping("/visits")
    public ResponseEntity<CompanyVisitDto> scheduleVisit(@RequestBody VisitCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitService.scheduleVisit(createDto));
    }

    @PutMapping("/visits/{id}")
    public ResponseEntity<CompanyVisitDto> updateVisit(
            @PathVariable Long id,
            @RequestBody VisitUpdateDto updateDto) {
        return ResponseEntity.ok(visitService.updateVisit(id, updateDto));
    }

    @PutMapping("/visits/{id}/status")
    public ResponseEntity<CompanyVisitDto> updateVisitStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        return ResponseEntity.ok(visitService.updateVisitStatus(id, isActive));
    }


    ///////////////////--------------------------------------------------------->>>>>>>>>>>>
    /// 
    

}
