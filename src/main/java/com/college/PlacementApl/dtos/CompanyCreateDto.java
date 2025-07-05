package com.college.PlacementApl.dtos;

import org.hibernate.annotations.processing.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateDto {

    private String name;

    private String description;

    private String website;

    private String contactEmail;

    private String contactPhone;
}
