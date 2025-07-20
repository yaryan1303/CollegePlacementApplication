package com.college.PlacementApl.Service;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.Company;
import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Model.PlacementRecord;
import com.college.PlacementApl.Model.PlacementStatus;
import com.college.PlacementApl.Model.StudentApplication;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Repository.ApplicationRepository;
import com.college.PlacementApl.Repository.CompanyRepository;
import com.college.PlacementApl.Repository.CompanyVisitRepository;
import com.college.PlacementApl.Repository.PlacementRecordRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.dtos.ApplicationDto;
import com.college.PlacementApl.dtos.ApplicationRequestDto;
import com.college.PlacementApl.dtos.ApplicationResponseDto;
import com.college.PlacementApl.dtos.CompanyBasicDto;
import com.college.PlacementApl.dtos.CompanyStatsDto;
import com.college.PlacementApl.utilites.ApplicationStatus;
import com.college.PlacementApl.utilites.BusinessException;
import com.college.PlacementApl.utilites.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ApplicationService {

   private StudentDetailsRepository studentRepository;

    private CompanyVisitRepository companyVisitRepository;

    private ApplicationRepository applicationRepository;

    private EmailService emailService;

    private CompanyRepository companyRepository;

    private ModelMapper modelMapper;

    private PlacementRecordRepository placementRepository;


    @Autowired
    public ApplicationService(CompanyVisitRepository companyVisitRepository,
            ApplicationRepository applicationRepository, EmailService emailService, CompanyRepository companyRepository,
            ModelMapper modelMapper, StudentDetailsRepository studentRepository, PlacementRecordRepository placementRepository)
            {
                this.companyVisitRepository = companyVisitRepository;
                this.applicationRepository = applicationRepository;
                this.emailService = emailService;
                this.companyRepository = companyRepository;
                this.modelMapper = modelMapper;
                this.studentRepository = studentRepository;
                this.placementRepository = placementRepository;

            }




     ///////////// ------------------------------------------------>>>>>>>>>>>>>>>>>>>>>.
    /// Apply for company that visit company
    public ApplicationDto applyForCompany(Long userId, ApplicationRequestDto applicationRequest) {
        StudentDetails student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        CompanyVisit visit = companyVisitRepository.findById(applicationRequest.getVisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Company visit not found"));

        if (!visit.isActive()) {
            throw new BusinessException("Cannot apply for an inactive company visit");
        }

        System.out.println(visit.getEligibilityCriteria());

        Double visitcgpa=Double.parseDouble(visit.getEligibilityCriteria());
        Double studentcgpa=student.getCgpa();

        if(studentcgpa<visitcgpa)
        {
            throw new BusinessException("You are not Eligible for this Application because your cgpa is less");
        }

        if(!visit.getBatchYear().equals(student.getBatchYear()))
        {
            System.out.println(visit.getBatchYear());
            System.out.println(student.getBatchYear());
            throw new BusinessException("You are not Eligible for this Application because your batch does not match");
        }



        if (applicationRepository.existsByStudentAndVisit(student, visit)) {
            throw new BusinessException("You have already applied for this company visit");
        }

        StudentApplication application = new StudentApplication();
        application.setStudent(student);
        application.setVisit(visit);

        application = applicationRepository.save(application);
        return modelMapper.map(application, ApplicationDto.class);

    }

    public List<ApplicationResponseDto> getAllApplcationsDetailsOfUser(Long userId) {
        StudentDetails student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Long studentId = student.getStudentId();
        List<StudentApplication> applications = applicationRepository.findByStudent_StudentId(studentId);

        return applications.stream()
                .map(this::ApplicationToApplicationResponseDTO)
                .toList();

    }

    private ApplicationResponseDto ApplicationToApplicationResponseDTO(StudentApplication application) {
        CompanyVisit visit = application.getVisit();
        Company company = visit.getCompany();
        StudentDetails student = application.getStudent();

        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setApplicationId(application.getApplicationId());
        dto.setCompany(new CompanyBasicDto(company.getCompanyId(), company.getName(), company.getWebsite()));
        dto.setJobPositions(visit.getJobPositions());
        dto.setSalaryPackage(visit.getSalaryPackage());
        dto.setApplicationStatus(application.getStatus().toString());
        dto.setFeedback(application.getFeedback());
        dto.setApplicationDate(application.getApplicationDate().toString());
        dto.setVisitDate(visit.getVisitDate());
        dto.setApplicationDeadline(visit.getApplicationDeadline());
        dto.setEligibilityCriteria(visit.getEligibilityCriteria());
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setRollNumber(student.getRollNumber());
        dto.setDepartment(student.getDepartment().getName());

        return dto;

    }

    ////////////////// -------------------------------------------------->>>>>>>>>>>>>>>>>>>>>
    /// Application by status ADMIN SIDE
    public List<ApplicationResponseDto> getAllApplications(ApplicationStatus status) {
        List<StudentApplication> applications = applicationRepository.findByStatus(status);

        return applications.stream()
                .map(this::ApplicationToApplicationResponseDTO)
                .toList();
    }

    ////////////////// -------------------------------------------------->>>>>>>>>>>>>>>>>>>>>
    /// Application by Company Name ADMIN SIDE
    public List<ApplicationResponseDto> getApplicationsByCompanyName(String companyName) {
        List<StudentApplication> applications = applicationRepository.findByCompanyNameContaining(companyName);

        return applications.stream()
                .map(this::ApplicationToApplicationResponseDTO)
                .toList();

    }

    // Update Application Status and also save placed Student in placement Table

    @Transactional
    public String updateApplicationStatus(Long id, ApplicationStatus status, String feedback) {
        StudentApplication application = applicationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Store previous status for comparison
        ApplicationStatus previousStatus = application.getStatus();
        
        application.setStatus(status);
        application.setFeedback(feedback);
        application = applicationRepository.save(application);

        // Send email only if status actually changed
        if (!status.equals(previousStatus)) {
            sendStatusUpdateEmail(application, status, feedback);
        }

        if (status == ApplicationStatus.SELECTED) {
            createPlacementRecord(application);
        }

        return "Application status updated successfully";
    }
    
    private void sendStatusUpdateEmail(StudentApplication application, 
                                     ApplicationStatus status, String feedback) {
        StudentDetails student = studentRepository.findById(application.getStudent().getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        emailService.sendApplicationStatusEmail(
            student.getUser().getEmail(),
            student.getFirstName() + " " + student.getLastName(),
            application.getVisit().getCompany().getName(),
            application.getVisit().getJobPositions(),
            status,
            feedback
        );
     }

    private void createPlacementRecord(StudentApplication application) {

        boolean placementExists = placementRepository.existsByStudentAndCompany(
                application.getStudent(),
                application.getVisit().getCompany());

        if (placementExists) {
            throw new BusinessException("Placement record already exists for this student and company");
        }

        PlacementRecord record = new PlacementRecord();
        record.setStudent(application.getStudent());
        record.setCompany(application.getVisit().getCompany());
        record.setVisit(application.getVisit());
        record.setPosition(application.getVisit().getJobPositions());
        record.setSalaryPackage(application.getVisit().getSalaryPackage());
        record.setPlacementDate(LocalDate.now());

        placementRepository.save(record);

        // Update student status
        StudentDetails student = application.getStudent();
        student.setCurrentStatus(PlacementStatus.PLACED);
        studentRepository.save(student);
    }

    /////////// ---------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /// Company Reports
    public List<CompanyStatsDto> getCompanyStatistics() {
        return companyRepository.getCompanyStats();
    }

}
