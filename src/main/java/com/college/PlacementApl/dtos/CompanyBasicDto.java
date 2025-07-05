package com.college.PlacementApl.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBasicDto {
    private Long companyId;
    private String name;
    private String website;
}

