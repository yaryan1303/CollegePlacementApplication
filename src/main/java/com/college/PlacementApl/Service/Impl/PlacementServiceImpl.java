package com.college.PlacementApl.Service.Impl;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.college.PlacementApl.Model.PlacementRecord;
import com.college.PlacementApl.Model.PlacementStatus;
import com.college.PlacementApl.Repository.PlacementRecordRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.Service.Placement_Service;
import com.college.PlacementApl.dtos.BatchStatsDto;
import com.college.PlacementApl.dtos.PlacementRecordDto;
import com.college.PlacementApl.dtos.PlacementStatsDto;

@Service
public class PlacementServiceImpl implements Placement_Service {

    private PlacementRecordRepository placementRepository;

    private StudentDetailsRepository studentRepository;

    



    @Autowired
    public PlacementServiceImpl(PlacementRecordRepository placementRepository, StudentDetailsRepository studentRepository) {
        this.placementRepository = placementRepository;
        this.studentRepository = studentRepository;
       
        
    }

    public List<PlacementRecordDto> getPlacementRecords(Integer batchYear, String companyName) {
        List<PlacementRecord> records = placementRepository.findByBatchYearOrCompanyName(batchYear, companyName);

        System.out.println(records+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        return records.stream()
                .map(this::convertToPlacementRecordDto)
                .collect(Collectors.toList());

    }


    /////////////////////---------------------------------------------->>>>>>>>>>>>>>>>>>>>>>
    /// Placement Summary
    public PlacementStatsDto getPlacementSummary() {
        Long totalStudents = studentRepository.count();
        Long placedStudents = studentRepository.countByCurrentStatus(PlacementStatus.PLACED);

        Double placementPercentage = totalStudents == 0 ? 0.0 : (placedStudents * 100.0) / totalStudents;

        List<BatchStatsDto> batchStats = studentRepository.findBatchWisePlacementStats();

        return new PlacementStatsDto(totalStudents, placedStudents, placementPercentage, batchStats);
    }

    @Override
    public Map<String, Map<String, List<PlacementRecordDto>>> getBranchYearWisePlacements() {
        List<PlacementRecord> records = placementRepository.findAll();

        return records.stream()
            .collect(Collectors.groupingBy(
                record -> record.getStudent().getDepartment().getName(),
                Collectors.groupingBy(
                    record -> String.valueOf(record.getStudent().getBatchYear()),
                    Collectors.mapping(this::convertToPlacementRecordDto, Collectors.toList())
                )
            ));
    }

   @Override
public Map<Integer, Long> getPlacementCountByBatchYear() {
    List<PlacementRecord> records = placementRepository.findAll();

    return records.stream()
            .collect(Collectors.groupingBy(
                    record -> record.getStudent().getBatchYear(),
                    Collectors.mapping(
                            record -> record.getStudent().getStudentId(),
                            Collectors.collectingAndThen(
                                    Collectors.toSet(), // store unique student IDs
                                    set -> (long) set.size() // count unique IDs
                            )
                    )
            ));
}

   

    private PlacementRecordDto convertToPlacementRecordDto(PlacementRecord record) {
        PlacementRecordDto dto = new PlacementRecordDto();
        dto.setRecordId(record.getRecordId());
        dto.setStudentId(record.getStudent().getStudentId());
        dto.setStudentName(record.getStudent().getFirstName() + " " + record.getStudent().getLastName());
        dto.setCompanyId(record.getCompany().getCompanyId());
        dto.setCompanyName(record.getCompany().getName());
        dto.setVisitId(record.getVisit().getVisitId());
        dto.setPosition(record.getPosition());
        dto.setSalaryPackage(record.getSalaryPackage());
        dto.setPlacementDate(record.getPlacementDate());
        dto.setInternship(record.isInternship());
        return dto;
    }

   
}

