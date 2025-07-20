package com.college.PlacementApl.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.Company;
import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Repository.CompanyRepository;
import com.college.PlacementApl.Repository.CompanyVisitRepository;
import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.VisitCreateDto;
import com.college.PlacementApl.dtos.VisitUpdateDto;
import com.college.PlacementApl.utilites.ResourceNotFoundException;

@Service
public class AdminVisitService {

   private  CompanyVisitRepository visitRepository;
    private  CompanyRepository companyRepository;

    @Autowired
    public AdminVisitService(CompanyVisitRepository visitRepository, CompanyRepository companyRepository) {
        this.visitRepository = visitRepository;
        this.companyRepository = companyRepository;
    }



    public CompanyVisitDto scheduleVisit(VisitCreateDto createDto) {
        Company company = companyRepository.findById(createDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        CompanyVisit visit = new CompanyVisit();
        visit.setCompany(company);
        visit.setVisitDate(createDto.getVisitDate());
        visit.setApplicationDeadline(createDto.getApplication_end_date());
        visit.setJobPositions(createDto.getJobPositions());
        visit.setSalaryPackage(createDto.getSalaryPackage());
        visit.setEligibilityCriteria(createDto.getEligibilityCriteria());
        visit.setBatchYear(createDto.getBatchYear());
        visit.setActive(true);  // Assuming new visits are active by default

        visit = visitRepository.save(visit);
        return convertToDto(visit);
    }

    public CompanyVisitDto updateVisit(Long id, VisitUpdateDto updateDto) {
        CompanyVisit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        visit.setVisitDate(updateDto.getVisitDate());
        visit.setApplicationDeadline(updateDto.getApplication_end_date());
        visit.setJobPositions(updateDto.getJobPositions());
        visit.setSalaryPackage(updateDto.getSalaryPackage());
        visit.setEligibilityCriteria(updateDto.getEligibilityCriteria());

        visit = visitRepository.save(visit);
        return convertToDto(visit);
    }

    public CompanyVisitDto updateVisitStatus(Long id, boolean isActive) {
        CompanyVisit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        visit.setActive(isActive);
        visit = visitRepository.save(visit);
        return convertToDto(visit);
    }

    private CompanyVisitDto convertToDto(CompanyVisit visit) {
        CompanyVisitDto dto = new CompanyVisitDto();
        dto.setVisitId(visit.getVisitId());
        dto.setCompanyId(visit.getCompany().getCompanyId());
        dto.setCompanyName(visit.getCompany().getName());
        dto.setVisitDate(visit.getVisitDate());
        dto.setApplication_end_data(visit.getApplicationDeadline());
        dto.setJobPositions(visit.getJobPositions());
        dto.setSalaryPackage(visit.getSalaryPackage());
        dto.setEligibilityCriteria(visit.getEligibilityCriteria());
        dto.setBatchYear(visit.getBatchYear());
        dto.setActive(visit.isActive());
        return dto;
    }

}
