package com.college.PlacementApl.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementStatsDto {
    private Long totalStudents;
    private Long placedStudents;
    private Double placementPercentage;
    private List<BatchStatsDto> batchWiseStats;

}
