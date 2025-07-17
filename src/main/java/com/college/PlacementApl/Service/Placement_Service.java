package com.college.PlacementApl.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.PlacementRecord;
import com.college.PlacementApl.Model.PlacementStatus;
import com.college.PlacementApl.Repository.PlacementRecordRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.dtos.BatchStatsDto;
import com.college.PlacementApl.dtos.PlacementRecordDto;
import com.college.PlacementApl.dtos.PlacementStatsDto;

@Service
public class Placement_Service {

    private PlacementRecordRepository placementRepository;

    private StudentDetailsRepository studentRepository;

    



    @Autowired
    public Placement_Service(PlacementRecordRepository placementRepository, StudentDetailsRepository studentRepository) {
        this.placementRepository = placementRepository;
        this.studentRepository = studentRepository;
       
        
    }

    public List<PlacementRecordDto> getPlacementRecords(Integer batchYear, String companyName) {
        List<PlacementRecord> records = placementRepository.findByBatchYearOrCompanyName(batchYear, companyName);

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
