package com.college.PlacementApl.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Repository.CompanyVisitRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.ResumeAnalyais.GroqResumeService;
import com.college.PlacementApl.dtos.AnswerEvaluationResult;
import com.college.PlacementApl.dtos.AtsScoreResult;
import com.college.PlacementApl.dtos.BatchStatsDto;
import com.college.PlacementApl.dtos.ChatRequest;
import com.college.PlacementApl.dtos.ChatResponse;
import com.college.PlacementApl.dtos.PlacementPredictionRequest;
import com.college.PlacementApl.dtos.PlacementPredictionResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiChatService {

    private final GroqResumeService groqService;
    private final CompanyVisitRepository visitRepository;
    private final StudentDetailsRepository studentRepository;
    private final ObjectMapper objectMapper;

    public AiChatService(GroqResumeService groqService,
            CompanyVisitRepository visitRepository,
            StudentDetailsRepository studentRepository) {
        this.groqService = groqService;
        this.visitRepository = visitRepository;
        this.studentRepository = studentRepository;
        this.objectMapper = new ObjectMapper();
    }

    // ==================== FEATURE 3: CHATBOT ====================

    public ChatResponse chat(ChatRequest request) {
        String systemContext = buildChatContext(request.getUserId());
        String reply = groqService.chatWithStudent(request.getMessage(), systemContext).join();
        return new ChatResponse(reply, LocalDateTime.now());
    }

    private String buildChatContext(Long userId) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("You are a helpful placement assistant for a college placement portal. ");
        ctx.append("Help students with placement-related queries. Be concise and friendly.\n\n");

        // Inject active company visits as context
        List<CompanyVisit> activeVisits = visitRepository
                .findByIsActiveTrueAndApplicationDeadlineAfter(LocalDate.now());

        if (!activeVisits.isEmpty()) {
            ctx.append("Currently active company visits:\n");
            activeVisits.stream().limit(5).forEach(v -> {
                ctx.append(String.format("- %s: %s | Salary: %s | Deadline: %s | Batch: %s\n",
                        v.getCompany().getName(),
                        v.getJobPositions() != null ? v.getJobPositions() : "N/A",
                        v.getSalaryPackage() != null ? v.getSalaryPackage() : "N/A",
                        v.getApplicationDeadline(),
                        v.getBatchYear() != null ? v.getBatchYear() : "All"));
            });
            ctx.append("\n");
        }

        // Inject student profile if userId provided
        if (userId != null) {
            studentRepository.findByUserId(userId).ifPresent(student -> {
                ctx.append(String.format(
                        "The student asking is: %s %s | CGPA: %.2f | Dept: %s | Batch: %d | Status: %s\n",
                        student.getFirstName(), student.getLastName(),
                        student.getCgpa() != null ? student.getCgpa() : 0.0,
                        student.getDepartment() != null ? student.getDepartment().getName() : "N/A",
                        student.getBatchYear() != null ? student.getBatchYear() : 0,
                        student.getCurrentStatus()));
            });
        }

        ctx.append("\nAnswer based on this context. If unsure, say so politely.");
        return ctx.toString();
    }

    // ==================== FEATURE 1: PLACEMENT PREDICTOR ====================

    public PlacementPredictionResult predictPlacement(PlacementPredictionRequest request) {
        // Build historical stats string from DB
        List<BatchStatsDto> batchStats = studentRepository.findBatchWisePlacementStats();
        String historicalStats = batchStats.stream()
                .map(b -> String.format("Batch %d: %d/%d placed (%.1f%%)",
                        b.getBatchYear(), b.getPlacedStudents(), b.getTotalStudents(),
                        b.getPlacementPercentage()))
                .collect(Collectors.joining("\n"));

        List<String> skills = request.getSkills() != null ? request.getSkills() : Collections.emptyList();
        double cgpa = request.getCgpa() != null ? request.getCgpa() : 0.0;
        String department = request.getDepartment() != null ? request.getDepartment() : "Unknown";
        int batchYear = request.getBatchYear() != null ? request.getBatchYear() : 0;

        String raw = groqService.predictPlacement(cgpa, skills, department, batchYear, historicalStats).join();
        return parseJson(raw, PlacementPredictionResult.class, defaultPrediction());
    }

    // ==================== HELPERS ====================

    public <T> T parseJson(String json, Class<T> clazz, T defaultValue) {
        try {
            String cleaned = cleanJson(json);
            return objectMapper.readValue(cleaned, clazz);
        } catch (Exception e) {
            log.warn("Failed to parse JSON for {}: {}", clazz.getSimpleName(), e.getMessage());
            return defaultValue;
        }
    }

    public String cleanJson(String raw) {
        if (raw == null) return "{}";
        return raw.trim()
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("\\s*```$", "")
                .trim();
    }

    private PlacementPredictionResult defaultPrediction() {
        PlacementPredictionResult r = new PlacementPredictionResult();
        r.setProbability(50);
        r.setLevel("MEDIUM");
        r.setReasoning("Prediction service temporarily unavailable. Please try again.");
        r.setStrengthAreas(Collections.emptyList());
        r.setImprovementAreas(Collections.emptyList());
        r.setRecommendedActions(Collections.emptyList());
        return r;
    }

    public AnswerEvaluationResult defaultEvaluation() {
        AnswerEvaluationResult r = new AnswerEvaluationResult();
        r.setScore(5);
        r.setGrade("Average");
        r.setFeedback("Evaluation service temporarily unavailable.");
        r.setKeyPointsCovered(Collections.emptyList());
        r.setKeyPointsMissed(Collections.emptyList());
        r.setImprovedAnswer("");
        return r;
    }

    public AtsScoreResult defaultAtsScore() {
        AtsScoreResult r = new AtsScoreResult();
        r.setScore(50);
        r.setRating("Average");
        r.setStrengths(Collections.emptyList());
        r.setWeaknesses(Collections.emptyList());
        r.setImprovements(Collections.emptyList());
        r.setKeywordsMissing(Collections.emptyList());
        return r;
    }
}
