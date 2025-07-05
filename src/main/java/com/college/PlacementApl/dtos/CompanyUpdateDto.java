package com.college.PlacementApl.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDto {

    private String name;

    private String description;

    private String website;

    private String contactEmail;

    private String contactPhone;
}
