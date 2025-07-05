package com.college.PlacementApl.dtos;

import java.time.LocalDateTime;

import com.college.PlacementApl.utilites.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusDto {
    private ApplicationStatus status;
    private String feedback;
    private LocalDateTime lastUpdated;
}