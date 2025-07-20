package com.college.PlacementApl.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.college.PlacementApl.Model.Department;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Security.JwtUtils;
import com.college.PlacementApl.Service.ApplicationService;
import com.college.PlacementApl.Service.DepartmentService;
import com.college.PlacementApl.Service.PlacementRecordService;
import com.college.PlacementApl.Service.Placement_Service;
import com.college.PlacementApl.Service.UserService;
import com.college.PlacementApl.Service.companyService;
import com.college.PlacementApl.dtos.ApplicationRequestDto;
import com.college.PlacementApl.dtos.ApplicationResponseDto;
import com.college.PlacementApl.dtos.ApplicationDto;
import com.college.PlacementApl.dtos.CompanyDto;
import com.college.PlacementApl.dtos.CompanyVisitResponseDto;
import com.college.PlacementApl.dtos.PlacementRecordDto;
import com.college.PlacementApl.dtos.PlacementRecordResponseDto;
import com.college.PlacementApl.dtos.StudentDetailsDto;
import com.college.PlacementApl.dtos.StudentDetailsResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    private companyService companyService;

    private Placement_Service placementService;

    private ApplicationService applicationService;

    private JwtUtils jwtUtils;

    private DepartmentService departmentService;

    private PlacementRecordService placementRecordService;



    @Autowired
    public UserController(UserService userService, companyService companyService, Placement_Service placementService,
            ApplicationService applicationService, JwtUtils jwtUtils, DepartmentService departmentService,
            PlacementRecordService placementRecordService) {
        this.userService = userService;
        this.companyService = companyService;
        this.placementService = placementService;
        this.applicationService = applicationService;
        this.jwtUtils = jwtUtils;
        this.departmentService = departmentService;
        this.placementRecordService = placementRecordService;
    }

    // Department Controller

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }


    // Save Student Details
    @PostMapping("/me")
    public ResponseEntity<StudentDetailsResponseDto> SaveStudentDetails(HttpServletRequest request,
           @Valid @RequestBody StudentDetailsDto studentDetailsDto) {
        System.out.println(studentDetailsDto);
        Long userId = userService.getUserIdFromRequest(request);
        studentDetailsDto.setUserId(userId);

        StudentDetailsResponseDto saveStudent = userService.saveStudent(studentDetailsDto);

        return ResponseEntity.ok(saveStudent);
    }

    // GetStudent Details by using userId

    @GetMapping("/me/{userId}")
    public ResponseEntity<StudentDetailsResponseDto> getStudentDetails(@PathVariable Long userId) {
        StudentDetailsResponseDto studentDetails = userService.getStudentDetails(userId);

        return ResponseEntity.ok(studentDetails);
    }

    @PutMapping("/me/{studentId}")
    public ResponseEntity<StudentDetailsResponseDto> updateStudentDetails(
            HttpServletRequest request,
            @PathVariable Long studentId,
            @Valid @RequestBody StudentDetailsDto studentDetailsDto) {

        Long userId = userService.getUserIdFromRequest(request);

        // Validate ownership inside service layer
        StudentDetailsResponseDto updatedStudent = userService.updateStudentDetails(studentId, userId, studentDetailsDto);

        return ResponseEntity.ok(updatedStudent);
    }

    ///////////////// -------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>
    /// Company Info User Mode
    ///

    // @GetMapping("/companies")
    // public ResponseEntity<List<CompanyDto>> getAllCompanies() {
    //     return ResponseEntity.ok(companyService.getAllCompanies());
    // }

    // @GetMapping("/companies/{id}")
    // public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
    //     return ResponseEntity.ok(companyService.getCompanyById(id));
    // }

    @GetMapping("companies/visits")
    public List<CompanyVisitResponseDto> getActiveCompanyVisits() {
        return companyService.getActiveVisits();
    }

    @GetMapping("companies/visits/{id}")
    public ResponseEntity<CompanyVisitResponseDto> getCompanyVisitById(@PathVariable Long id) {
        System.out.println(id);
        return ResponseEntity.ok(companyService.getCompanyVisitById(id));
    }

    /////////////////////////// ------------------------------------------------->>>>>>>>>
    /// Apply in Company that visiting in College
    ///
    ///
    @PostMapping("/applications")
    public ResponseEntity<ApplicationDto> applyForCompany(HttpServletRequest request,
            @Valid @RequestBody ApplicationRequestDto applicationRequest) {
        Long userId = userService.getUserIdFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.applyForCompany(userId, applicationRequest));

    }

    // Get All Application Details of User
    @GetMapping("/applications")
    public List<ApplicationResponseDto> getAllApplcationsDetails(HttpServletRequest request) {
        Long userId = userService.getUserIdFromRequest(request);

        List<ApplicationResponseDto> allApplcationsDetails = applicationService.getAllApplcationsDetailsOfUser(userId);

        return allApplcationsDetails;
    }

    //////////////////// ---------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>
    /// Placemement Records
    ///
    ///
    /// GetplacementRecords By BatchYear or CompanyName
    @GetMapping("/records")
    public ResponseEntity<List<PlacementRecordDto>> getPlacementRecords(
            @RequestParam(required = false) Integer batchYear,
            @RequestParam(required = false) String companyName) {
        return ResponseEntity.ok(placementService.getPlacementRecords(batchYear, companyName));
    }

    @GetMapping("/placementsRecords")
    public ResponseEntity<List<PlacementRecordResponseDto>> getAllPlacements() {
        return ResponseEntity.ok(placementRecordService.getAllPlacementRecords());
    }

}
