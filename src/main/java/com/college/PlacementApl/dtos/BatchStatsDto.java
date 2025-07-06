package com.college.PlacementApl.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchStatsDto {
    private Integer batchYear;
    private Long totalStudents;
    private Long placedStudents;
    private Double placementPercentage;

}
