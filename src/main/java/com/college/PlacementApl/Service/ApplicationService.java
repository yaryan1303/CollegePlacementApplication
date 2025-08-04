package com.college.PlacementApl.Service;

import com.college.PlacementApl.dtos.ApplicationDto;
import com.college.PlacementApl.dtos.ApplicationRequestDto;
import com.college.PlacementApl.dtos.ApplicationResponseDto;
import com.college.PlacementApl.dtos.CompanyStatsDto;
import com.college.PlacementApl.utilites.ApplicationStatus;

import java.util.List;

public interface ApplicationService{

    ApplicationDto applyForCompany(Long userId, ApplicationRequestDto applicationRequest);

    List<ApplicationResponseDto> getAllApplcationsDetailsOfUser(Long userId);

    List<ApplicationResponseDto> getAllApplications(ApplicationStatus status);

    List<ApplicationResponseDto> getApplicationsByCompanyName(String companyName);

    String updateApplicationStatus(Long id, ApplicationStatus status, String feedback);

    List<CompanyStatsDto> getCompanyStatistics();
}
