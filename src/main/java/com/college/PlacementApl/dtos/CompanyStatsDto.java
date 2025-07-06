package com.college.PlacementApl.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatsDto {
    private String companyName;
    private Long totalVisits;
    private Long totalApplications;
    private Long totalPlacements;
}

