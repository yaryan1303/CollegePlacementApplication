package com.college.PlacementApl.ResumeAnalyais;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.college.PlacementApl.Model.CompanyVisit;
import com.college.PlacementApl.Repository.CompanyVisitRepository;
import com.college.PlacementApl.Service.AiChatService;
import com.college.PlacementApl.dtos.AtsScoreResult;
import com.college.PlacementApl.dtos.Recommendation;
import com.college.PlacementApl.dtos.ResumeAnalysisResult;
import com.college.PlacementApl.dtos.ResumeProfile;
import com.college.PlacementApl.dtos.SmartRecommendation;
import com.college.PlacementApl.dtos.SmartRecommendResult;



@Service
public class ResumeAnalysisService {

    private final ResumeParser parser;
    private final CompanyVisitRepository companyVisitRepository;
    private final GroqResumeService groqResumeService;
    private final AiChatService aiChatService;

    public ResumeAnalysisService(ResumeParser parser,
                                 CompanyVisitRepository companyVisitRepository,
                                 GroqResumeService groqResumeService,
                                 AiChatService aiChatService) {
        this.parser = parser;
        this.companyVisitRepository = companyVisitRepository;
        this.groqResumeService = groqResumeService;
        this.aiChatService = aiChatService;
    }

    public ResumeAnalysisResult analyze(MultipartFile file) throws Exception {
        ResumeProfile profile = parser.parse(file);

        List<CompanyVisit> visits = companyVisitRepository.findByIsActiveTrueAndApplicationDeadlineAfter(LocalDate.now());

        List<Recommendation> recs = new ArrayList<>();
        for (CompanyVisit v : visits) {
            double score = scoreVisit(profile, v);
            if (score > 0.05) {
                String reason = generateReason(profile, v);
                recs.add(new Recommendation(v.getVisitId(), v.getCompany().getName(), v.getJobPositions(),
                        v.getSalaryPackage(), v.getVisitDate(), v.getApplicationDeadline(),
                        v.getEligibilityCriteria(), v.getBatchYear(), score, reason));
            }
        }

        recs = recs.stream()
                .sorted(Comparator.comparingDouble(Recommendation::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());

        ResumeAnalysisResult result = new ResumeAnalysisResult();
        result.setProfile(profile);
        result.setRecommendations(recs);

        // AI resume feedback
        String ai = groqResumeService.generateFeedback(profile.getRawText());
        result.setAiFeedback(ai);

        // Feature 5: ATS Resume Score
        String atsRaw = groqResumeService.generateAtsScore(profile.getRawText());
        AtsScoreResult atsScore = aiChatService.parseJson(atsRaw, AtsScoreResult.class, aiChatService.defaultAtsScore());
        result.setAtsScore(atsScore);

        return result;
    }

    // Feature 2: Smart Semantic Company Recommender
    public SmartRecommendResult smartRecommend(MultipartFile file) throws Exception {
        ResumeProfile profile = parser.parse(file);
        List<CompanyVisit> visits = companyVisitRepository.findByIsActiveTrueAndApplicationDeadlineAfter(LocalDate.now());

        // Keyword pre-filter to top 5 to limit LLM calls
        List<CompanyVisit> topCandidates = visits.stream()
                .sorted(Comparator.comparingDouble(v -> -scoreVisit(profile, (CompanyVisit) v)))
                .limit(5)
                .collect(Collectors.toList());

        String studentSkills = String.join(", ",
                Optional.ofNullable(profile.getSkills()).orElse(Collections.emptyList()));

        List<SmartRecommendation> smartRecs = new ArrayList<>();
        for (CompanyVisit v : topCandidates) {
            String jobDesc = (v.getJobPositions() != null ? v.getJobPositions() : "") + " " +
                             (v.getEligibilityCriteria() != null ? v.getEligibilityCriteria() : "");
            String raw = groqResumeService.semanticMatchCompany(studentSkills, jobDesc, v.getCompany().getName());
            SmartRecommendation sr = aiChatService.parseJson(raw, SmartRecommendation.class, new SmartRecommendation());
            sr.setVisitId(v.getVisitId());
            sr.setCompanyName(v.getCompany().getName());
            sr.setJobPositions(v.getJobPositions());
            sr.setSalaryPackage(v.getSalaryPackage());
            sr.setVisitDate(v.getVisitDate());
            sr.setApplicationDeadline(v.getApplicationDeadline());
            sr.setEligibilityCriteria(v.getEligibilityCriteria());
            sr.setBatchYear(v.getBatchYear());
            smartRecs.add(sr);
        }

        List<SmartRecommendation> sortedRecs = smartRecs.stream()
                .sorted(Comparator.comparingInt(r -> -(r.getMatchScore() != null ? r.getMatchScore() : 0)))
                .collect(Collectors.toList());

        // ATS score & feedback computed from the same parsed resume text
        String aiFeedback = groqResumeService.generateFeedback(profile.getRawText());
        String atsRaw = groqResumeService.generateAtsScore(profile.getRawText());
        AtsScoreResult atsScore = aiChatService.parseJson(atsRaw, AtsScoreResult.class, aiChatService.defaultAtsScore());

        SmartRecommendResult result = new SmartRecommendResult();
        result.setRecommendations(sortedRecs);
        result.setAtsScore(atsScore);
        result.setAiFeedback(aiFeedback);
        result.setProfile(profile);
        return result;
    }

    private double scoreVisit(ResumeProfile profile, CompanyVisit visit) {
        // Naive scoring: keyword overlap between skills and jobPositions + presence of batchYear or eligibility
        String jobText = (visit.getJobPositions() == null ? "" : visit.getJobPositions().toLowerCase()) + " " +
                         (visit.getEligibilityCriteria() == null ? "" : visit.getEligibilityCriteria().toLowerCase());

        // skill match score (Jaccard-like)
        Set<String> resumeSkills = new HashSet<>(Optional.ofNullable(profile.getSkills()).orElse(Collections.emptyList()));
        Set<String> jobSkills = new HashSet<>();
        for (String tok : jobText.split("[,;\\s]+")) jobSkills.add(tok.trim());

        long matches = resumeSkills.stream().filter(s -> jobSkills.contains(s.toLowerCase())).count();
        double skillScore = resumeSkills.isEmpty() ? 0.0 : (double) matches / resumeSkills.size();

        // bonus if experience mentions company-specific keywords
        double expBonus = 0.0;
        for (String exp : Optional.ofNullable(profile.getExperiences()).orElse(Collections.emptyList())) {
            if (exp.toLowerCase().contains(visit.getCompany().getName().toLowerCase().split("\\s+")[0])) {
                expBonus += 0.1;
            }
        }

        // eligibility / batch year check (simple)
        double batchScore = 0.0;
        if (visit.getBatchYear() != null && profile.getRawText() != null) {
            if (profile.getRawText().contains(String.valueOf(visit.getBatchYear()))) batchScore = 0.15;
        }

        double total = skillScore * 0.7 + expBonus + batchScore;
        // normalize to 0..1
        if (total > 1.0) total = 1.0;
        return Math.round(total * 100.0) / 100.0;
    }

    private String generateReason(ResumeProfile profile, CompanyVisit visit) {
        // Generate a short reason based on matching skills or batch or keywords
        List<String> reasons = new ArrayList<>();
        String jobText = (visit.getJobPositions() == null ? "" : visit.getJobPositions()).toLowerCase();
        for (String s : Optional.ofNullable(profile.getSkills()).orElse(Collections.emptyList())) {
            if (jobText.contains(s.toLowerCase())) reasons.add("Has skill: " + s);
        }
        if (visit.getBatchYear() != null && profile.getRawText().contains(String.valueOf(visit.getBatchYear()))) {
            reasons.add("Matches batch year: " + visit.getBatchYear());
        }
        if (reasons.isEmpty()) reasons.add("Partial keyword overlap with job description");
        return String.join("; ", reasons);
    }
}

