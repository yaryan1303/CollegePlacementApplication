package com.college.PlacementApl.Service;

import com.college.PlacementApl.dtos.CompanyVisitDto;
import com.college.PlacementApl.dtos.VisitCreateDto;
import com.college.PlacementApl.dtos.VisitUpdateDto;


public interface AdminVisitService {
    CompanyVisitDto scheduleVisit(VisitCreateDto createDto);

    CompanyVisitDto updateVisit(Long id, VisitUpdateDto updateDto);

    CompanyVisitDto updateVisitStatus(Long id, boolean isActive);

}
