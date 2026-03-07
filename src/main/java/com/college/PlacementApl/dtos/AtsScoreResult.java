package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtsScoreResult {
    private Integer score;
    private String rating;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> improvements;
    private List<String> keywordsMissing;
    private String error;
}
