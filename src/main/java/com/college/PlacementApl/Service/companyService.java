package com.college.PlacementApl.Service;

import com.college.PlacementApl.dtos.CompanyBasicDto;
import com.college.PlacementApl.dtos.CompanyCreateDto;
import com.college.PlacementApl.dtos.CompanyDto;
import com.college.PlacementApl.dtos.CompanyUpdateDto;
import com.college.PlacementApl.dtos.CompanyVisitResponseDto;

import java.util.List;

public interface companyService {

    CompanyDto createCompany(CompanyCreateDto createDto);

    CompanyDto updateCompany(Long id, CompanyUpdateDto updateDto);

    void deleteCompany(Long id);

    List<CompanyDto> getAllCompanies();

    CompanyDto getCompanyById(Long id);

    List<CompanyVisitResponseDto> getActiveVisits();

    CompanyVisitResponseDto getCompanyVisitById(Long id);
}
