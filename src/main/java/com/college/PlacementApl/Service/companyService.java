package com.college.PlacementApl.Service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.Company;
import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Model.StudentApplication;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Repository.ApplicationRepository;
import com.college.PlacementApl.Repository.CompanyRepository;
import com.college.PlacementApl.Repository.CompanyVisitRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.dtos.ApplicationDto;
import com.college.PlacementApl.dtos.ApplicationRequestDto;
import com.college.PlacementApl.dtos.CompanyBasicDto;
import com.college.PlacementApl.dtos.CompanyCreateDto;
import com.college.PlacementApl.dtos.CompanyDto;
import com.college.PlacementApl.dtos.CompanyUpdateDto;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.CompanyVisitResponseDto;
import com.college.PlacementApl.utilites.BusinessException;
import com.college.PlacementApl.utilites.ResourceNotFoundException;

@Service
public class companyService {

    private CompanyRepository companyRepository;

    private CompanyVisitRepository companyVisitRepository;

    private ApplicationRepository applicationRepository;

    private StudentDetailsRepository studentRepository;

    private ModelMapper modelMapper;

    @Autowired
    public companyService(CompanyRepository companyRepository, CompanyVisitRepository companyVisitRepository,
            ApplicationRepository applicationRepository, StudentDetailsRepository studentRepository,
            ModelMapper modelMapper) {
        this.companyRepository = companyRepository;
        this.companyVisitRepository = companyVisitRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }

    public CompanyDto createCompany(CompanyCreateDto createDto) {
        Company company = new Company();
        company.setName(createDto.getName());
        company.setDescription(createDto.getDescription());
        company.setWebsite(createDto.getWebsite());
        company.setContactEmail(createDto.getContactEmail());
        company.setContactPhone(createDto.getContactPhone());

        company = companyRepository.save(company);
        return convertToDto(company);
    }

    public CompanyDto updateCompany(Long id, CompanyUpdateDto updateDto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setName(updateDto.getName());
        company.setDescription(updateDto.getDescription());
        company.setWebsite(updateDto.getWebsite());
        company.setContactEmail(updateDto.getContactEmail());
        company.setContactPhone(updateDto.getContactPhone());

        company = companyRepository.save(company);
        return convertToDto(company);
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (company.getVisits() != null && !company.getVisits().isEmpty()) {
            throw new BusinessException("Cannot delete company with active visits");
        }

        companyRepository.delete(company);
    }

    // Manual Mapper Method
    private CompanyDto convertToDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setCompanyId(company.getCompanyId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setWebsite(company.getWebsite());
        dto.setContactEmail(company.getContactEmail());
        dto.setContactPhone(company.getContactPhone());
        return dto;
    }

    public List<CompanyDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::convertToDto)
                .toList();

    }

    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        return convertToDto(company);
    }

    ///////////// ----------------------------------------------->>>>>>>>>>
    /// Get Company Details which application are active
    public List<CompanyVisitResponseDto> getActiveVisits() {
        List<CompanyVisit> activeVisits = companyVisitRepository.findByIsActiveTrue();

        return activeVisits.stream()
                .map(this::mapToDTO)
                .toList();

    }

    private CompanyVisitResponseDto mapToDTO(CompanyVisit visit) {
        CompanyVisitResponseDto dto = new CompanyVisitResponseDto();

        dto.setVisitId(visit.getVisitId());
        dto.setCompany(new CompanyBasicDto(visit.getCompany().getCompanyId(), visit.getCompany().getName(),
                visit.getCompany().getWebsite()));
        dto.setVisitDate(visit.getVisitDate());
        dto.setApplicationDeadline(visit.getApplicationDeadline());
        dto.setJobPositions(visit.getJobPositions());
        dto.setSalaryPackage(visit.getSalaryPackage());
        dto.setEligibilityCriteria(visit.getEligibilityCriteria());
        dto.setBatchYear(visit.getBatchYear());

        return dto;
    }

    //////////// ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /// Get company visit details
    public CompanyVisitResponseDto getCompanyVisitById(Long id) {
        return companyVisitRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Company Visit not found with id: " + id));
    }

    public ApplicationDto applyForCompany(Long userId, ApplicationRequestDto applicationRequest) {
        StudentDetails student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        CompanyVisit visit = companyVisitRepository.findById(applicationRequest.getVisitId())
                .orElseThrow(() -> new ResourceNotFoundException("Company visit not found"));

        if (!visit.isActive()) {
            throw new BusinessException("Cannot apply for an inactive company visit");
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

}
