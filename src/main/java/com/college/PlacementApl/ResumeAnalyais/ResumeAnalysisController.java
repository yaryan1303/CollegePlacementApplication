package com.college.PlacementApl.ResumeAnalyais;


import java.util.HashMap;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.college.PlacementApl.dtos.ResumeAnalysisResult;
import com.college.PlacementApl.dtos.SmartRecommendResult;

@RestController
@RequestMapping("/api/resume")
@Validated
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeService;

    public ResumeAnalysisController(ResumeAnalysisService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeResume(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Please upload a resume PDF file");
            return ResponseEntity.badRequest().body(err);
        }
        try {
            ResumeAnalysisResult result = resumeService.analyze(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to analyze resume: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    // Feature 2: Smart Semantic Company Recommender
    // POST /api/resume/smart-recommend  (multipart/form-data, field: "file")
    @PostMapping(value = "/smart-recommend", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> smartRecommend(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Please upload a resume PDF file");
            return ResponseEntity.badRequest().body(err);
        }
        try {
            SmartRecommendResult result = resumeService.smartRecommend(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "Smart recommendation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
