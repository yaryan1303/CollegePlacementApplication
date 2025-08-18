package com.college.PlacementApl.dtos;

import java.util.List;
import lombok.Data;

@Data
public class ResumeProfile {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private List<String> experiences; // short text snippets per experience
    private String rawText; // full extracted text
}