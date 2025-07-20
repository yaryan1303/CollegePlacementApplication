package com.college.PlacementApl.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.PlacementRecord;
import com.college.PlacementApl.Repository.PlacementRecordRepository;
import com.college.PlacementApl.dtos.PlacementRecordResponseDto;


@Service
public class PlacementRecordService {

    @Autowired
    private PlacementRecordRepository placementRecordRepository;

    public List<PlacementRecordResponseDto> getAllPlacementRecords() {
        return placementRecordRepository.findAll().stream().map(record -> {
            PlacementRecordResponseDto dto = new PlacementRecordResponseDto();

            dto.setRecordId(record.getRecordId());
            dto.setStudentId(record.getStudent().getUser().getId());
            dto.setStudentName(record.getStudent().getFirstName() + " " + record.getStudent().getLastName());
            dto.setRollNumber(record.getStudent().getRollNumber());

            dto.setCompanyId(record.getCompany().getCompanyId());
            dto.setCompanyName(record.getCompany().getName());

            dto.setVisitId(record.getVisit().getVisitId());
            dto.setVisitDate(record.getVisit().getVisitDate());

            dto.setPosition(record.getPosition());
            dto.setSalaryPackage(record.getSalaryPackage());
            dto.setPlacementDate(record.getPlacementDate());
            dto.setInternship(record.isInternship());

            dto.setCreatedAt(record.getCreatedAt());
            dto.setUpdatedAt(record.getUpdatedAt());

            return dto;
        }).collect(Collectors.toList());
    }
}

