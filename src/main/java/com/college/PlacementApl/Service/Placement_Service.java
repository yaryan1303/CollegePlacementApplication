package com.college.PlacementApl.Service;

import java.util.List;
import java.util.Map;

import com.college.PlacementApl.dtos.PlacementRecordDto;
import com.college.PlacementApl.dtos.PlacementRecordResponseDto;
import com.college.PlacementApl.dtos.PlacementStatsDto;

public interface Placement_Service {

    List<PlacementRecordResponseDto> getPlacementRecords(Integer batchYear, String companyName);

    PlacementStatsDto getPlacementSummary();

   
    Map<String, Map<String, List<PlacementRecordDto>>> getBranchYearWisePlacements();

    Map<Integer, Long> getPlacementCountByBatchYear();

}
