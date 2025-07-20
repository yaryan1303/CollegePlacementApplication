package com.college.PlacementApl.dtos;



import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDto {
    
    @NotNull(message = "Visit ID is required")
    private Long visitId;
}
