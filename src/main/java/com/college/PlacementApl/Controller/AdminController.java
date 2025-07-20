package com.college.PlacementApl.Controller;

import java.io.IOException;
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

import com.college.PlacementApl.Model.Department;
import com.college.PlacementApl.Service.AdminVisitService;
import com.college.PlacementApl.Service.ApplicationService;
import com.college.PlacementApl.Service.DepartmentService;
import com.college.PlacementApl.Service.Placement_Service;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.Service.companyService;
import com.college.PlacementApl.dtos.ApplicationResponseDto;
import com.college.PlacementApl.dtos.CompanyCreateDto;
import com.college.PlacementApl.dtos.CompanyDto;
import com.college.PlacementApl.dtos.CompanyStatsDto;
import com.college.PlacementApl.dtos.CompanyUpdateDto;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.PlacementStatsDto;
import com.college.PlacementApl.dtos.StudentProfileDto;
import com.college.PlacementApl.dtos.VisitCreateDto;
import com.college.PlacementApl.dtos.VisitUpdateDto;
import com.college.PlacementApl.utilites.ApplicationStatus;
import com.college.PlacementApl.utilites.CompanyStatsExcelExporter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private UserService studentService;

    private companyService companyService;

    private AdminVisitService visitService;

    private Placement_Service placementService;

    private ApplicationService applicationService;

    private DepartmentService departmentService;



    @Autowired
    public AdminController(UserService studentService, companyService companyService, AdminVisitService visitService,
            Placement_Service placementService, ApplicationService applicationService, DepartmentService departmentService) {
        this.studentService = studentService;
        this.companyService = companyService;
        this.visitService = visitService;
        this.placementService = placementService;
        this.applicationService = applicationService;
        this.departmentService = departmentService;
    }

    // Category Controller 
    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody Department department) {
        Department createdDepartment = departmentService.createDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        }


    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id,@Valid @RequestBody Department department) {
        Department updatedDepartment = departmentService.updateDepartment(id, department);
        return ResponseEntity.ok(updatedDepartment);
    }


    

    //////////////// ----------------------------------------------------------------->>>>>>>>
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

    //////////////////////////////// --------------------------------------->>>>>>>>>>>>>>>>>>>>>>>
    /// Company Controller
    ///

    @PostMapping("/companies")
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyCreateDto createDto) {
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
            @Valid @RequestBody CompanyUpdateDto updateDto) {
        return ResponseEntity.ok(companyService.updateCompany(id, updateDto));
    }

    @DeleteMapping("/companies/{id}")
    public String deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return "Company deleted successfully";
    }

    /////////// ----------------------------------------------------------->>>>>>>>>
    /// Company Visit

    @PostMapping("/visits")
    public ResponseEntity<CompanyVisitDto> scheduleVisit(@Valid @RequestBody VisitCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitService.scheduleVisit(createDto));
    }

    @PutMapping("/visits/{id}")
    public ResponseEntity<CompanyVisitDto> updateVisit(
            @PathVariable Long id,
           @Valid @RequestBody VisitUpdateDto updateDto) {
        return ResponseEntity.ok(visitService.updateVisit(id, updateDto));
    }

    @PutMapping("/visits/{id}/status")
    public ResponseEntity<CompanyVisitDto> updateVisitStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        return ResponseEntity.ok(visitService.updateVisitStatus(id, isActive));
    }

    /////////////////// --------------------------------------------------------->>>>>>>>>>>>
    /// StudentApplication Fetures...........
    ///
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponseDto>> getAllApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.getAllApplications(status));

    }

    @GetMapping("applications/by-company")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsByCompany(
            @RequestParam String companyName) {
        return ResponseEntity.ok(applicationService.getApplicationsByCompanyName(companyName));
    }

    @PutMapping("applications/{id}")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status, feedback));
    }

    ////////////////////////// ----------------------------------------------->>>>>>>>>>>>>>>>>
    /// Get Placment Summary
    @GetMapping("/reports/placement-summary")
    public ResponseEntity<PlacementStatsDto> getPlacementSummary() {
        return ResponseEntity.ok(placementService.getPlacementSummary());
    }

    @GetMapping("/reports/company-stats")
    public ResponseEntity<List<CompanyStatsDto>> getCompanyStats() {
        return ResponseEntity.ok(applicationService.getCompanyStatistics());
    }

    @GetMapping("/reports/company-stats/export")
    public ResponseEntity<byte[]> exportCompanyStatsToExcel() throws IOException {
        List<CompanyStatsDto> stats = applicationService.getCompanyStatistics();
        byte[] excelData = CompanyStatsExcelExporter.exportToExcel(stats);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=company-stats.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
    }

}
