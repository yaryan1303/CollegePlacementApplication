package com.college.PlacementApl.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.college.PlacementApl.utilites.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long applicationId;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
}
