package com.college.PlacementApl.Service;

import com.college.PlacementApl.Model.InterviewQuestion;
import com.college.PlacementApl.Model.MockInterview;
import com.college.PlacementApl.Model.StudentDetails;
import com.college.PlacementApl.Repository.InterviewQuestionRepository;
import com.college.PlacementApl.Repository.MockInterviewRepository;
import com.college.PlacementApl.Repository.StudentDetailsRepository;
import com.college.PlacementApl.dtos.*;
import com.college.PlacementApl.utilites.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockInterviewService {
    
    private final MockInterviewRepository mockInterviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final GroqAIService groqAIService;
    private final WebClient webClient;
    private final StudentDetailsRepository studentDetailsRepository;
    
    public MockInterview startInterview(StartInterviewRequest request, Long studentId, String studentName) {
        // Check for active interview
        Optional<MockInterview> activeInterview = mockInterviewRepository.findActiveInterviewByStudentId(studentId);
        if (activeInterview.isPresent()) {
            throw new IllegalStateException("You have an active interview. Please complete it first.");
        }
        
        MockInterview interview = new MockInterview();
        interview.setStudentId(studentId);
        interview.setStudentName(studentName);
        interview.setTechnology(request.getTechnology());
        interview.setStartTime(LocalDateTime.now());
        interview.setTotalQuestions(request.getQuestionCount() != null ? request.getQuestionCount() : 10);
        interview.setQuestionsAnswered(0);
        
        MockInterview savedInterview = mockInterviewRepository.save(interview);
        
        // Generate initial questions
        generateInitialQuestions(savedInterview, request.getTechnology(), 
                               request.getDifficultyLevel(), interview.getTotalQuestions());
        
        return savedInterview;
    }
    
    private void generateInitialQuestions(MockInterview interview, String technology, 
                                        String difficultyLevel, Integer questionCount) {
        String prompt = String.format(
            "Generate %d interview questions for %s technology at %s level. " +
            "Include a mix of theoretical, practical, and behavioral questions. " +
            "Format each question as: QUESTION: [question text] DIFFICULTY: [EASY/MEDIUM/HARD] CATEGORY: [THEORETICAL/PRACTICAL/BEHAVIORAL]",
            questionCount, technology, difficultyLevel
        );
        
        try {
            String response = groqAIService.generateContent(prompt);
            parseAndSaveQuestions(response, interview);
        } catch (Exception e) {
            log.error("Error generating questions: {}", e.getMessage());
            // Fallback questions
            generateFallbackQuestions(interview, technology);
        }
    }
    
    private void parseAndSaveQuestions(String response, MockInterview interview) {
        String[] lines = response.split("\n");
        List<InterviewQuestion> questions = new ArrayList<>();
        int order = 1;
        
        for (String line : lines) {
            if (line.startsWith("QUESTION:")) {
                InterviewQuestion question = new InterviewQuestion();
                question.setMockInterview(interview);
                question.setQuestion(line.substring("QUESTION:".length()).trim());
                question.setQuestionOrder(order++);
                question.setIsFollowUp(false);
                questions.add(question);
            } else if (line.startsWith("DIFFICULTY:") && !questions.isEmpty()) {
                questions.get(questions.size() - 1).setDifficulty(line.substring("DIFFICULTY:".length()).trim());
            } else if (line.startsWith("CATEGORY:") && !questions.isEmpty()) {
                questions.get(questions.size() - 1).setCategory(line.substring("CATEGORY:".length()).trim());
            }
        }
        
        questionRepository.saveAll(questions);
    }
    
    private void generateFallbackQuestions(MockInterview interview, String technology) {
        List<InterviewQuestion> fallbackQuestions = Arrays.asList(
            createQuestion(interview, "What are the main features of " + technology + "?", "EASY", "THEORETICAL", 1),
            createQuestion(interview, "Explain the architecture of a typical " + technology + " application.", "MEDIUM", "THEORETICAL", 2),
            createQuestion(interview, "What are the best practices for " + technology + " development?", "MEDIUM", "PRACTICAL", 3),
            createQuestion(interview, "How do you handle errors in " + technology + "?", "MEDIUM", "PRACTICAL", 4),
            createQuestion(interview, "Describe a challenging project you worked on with " + technology + ".", "HARD", "BEHAVIORAL", 5)
        );
        
        questionRepository.saveAll(fallbackQuestions);
    }
    
    private InterviewQuestion createQuestion(MockInterview interview, String questionText, 
                                           String difficulty, String category, int order) {
        InterviewQuestion question = new InterviewQuestion();
        question.setMockInterview(interview);
        question.setQuestion(questionText);
        question.setDifficulty(difficulty);
        question.setCategory(category);
        question.setQuestionOrder(order);
        question.setIsFollowUp(false);
        return question;
    }
    
    public List<InterviewQuestionDTO> getNextQuestion(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found"));
            
        List<InterviewQuestion> questions = questionRepository
            .findByMockInterviewIdOrderByQuestionOrder(interviewId);
            
        // Find unanswered questions
        Optional<InterviewQuestion> nextQuestion = questions.stream()
            .filter(q -> q.getUserAnswer() == null)
            .findFirst();
            
        if (nextQuestion.isPresent()) {
            return List.of(convertToDTO(nextQuestion.get()));
        }
        
        // If no more questions, check for follow-ups
        List<InterviewQuestion> followUps = questionRepository
            .findByMockInterviewIdAndIsFollowUp(interviewId, true);
            
        Optional<InterviewQuestion> nextFollowUp = followUps.stream()
            .filter(q -> q.getUserAnswer() == null)
            .findFirst();
            
        if (nextFollowUp.isPresent()) {
            return List.of(convertToDTO(nextFollowUp.get()));
        }
        
        // No more questions
        completeInterview(interview);
        return List.of();
    }
    
    public AIAnalysisResponse submitAnswer(AnswerSubmissionDTO submission) {
        InterviewQuestion question = questionRepository.findById(submission.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
            
        question.setUserAnswer(submission.getAnswer());
        
        // Analyze answer using AI
        AIAnalysisResponse analysis = analyzeAnswer(question.getQuestion(), submission.getAnswer(), 
                                                  question.getDifficulty(), question.getCategory());
        
        question.setAiFeedback(analysis.getFeedback());
        question.setClarityScore(analysis.getClarityScore());
        question.setCorrectnessScore(analysis.getCorrectnessScore());
        question.setConfidenceScore(analysis.getConfidenceScore());
        question.setOverallScore(analysis.getOverallScore());
        
        questionRepository.save(question);
        
        // Update interview progress
        MockInterview interview = question.getMockInterview();
        interview.setQuestionsAnswered(interview.getQuestionsAnswered() + 1);
        mockInterviewRepository.save(interview);
        
        // Generate follow-up question if needed
        if (analysis.getRequiresFollowUp() && analysis.getSuggestedFollowUp() != null) {
            generateFollowUpQuestion(interview, question, analysis.getSuggestedFollowUp());
        }
        
        return analysis;
    }
    
    private AIAnalysisResponse analyzeAnswer(String question, String answer, String difficulty, String category) {
        String prompt = String.format(
            "Analyze this interview answer and provide detailed feedback.\n\n" +
            "QUESTION: %s\nANSWER: %s\nDIFFICULTY: %s\nCATEGORY: %s\n\n" +
            "Provide analysis in this exact format:\n" +
            "FEEDBACK: [comprehensive feedback]\n" +
            "CLARITY_SCORE: [0-10]\n" +
            "CORRECTNESS_SCORE: [0-10]\n" +
            "CONFIDENCE_SCORE: [0-10]\n" +
            "OVERALL_SCORE: [0-10]\n" +
            "REQUIRES_FOLLOW_UP: [true/false]\n" +
            "SUGGESTED_FOLLOW_UP: [follow-up question or null]",
            question, answer, difficulty, category
        );
        
        try {
            String response = groqAIService.generateContent(prompt);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            log.error("Error analyzing answer: {}", e.getMessage());
            return createDefaultAnalysis();
        }
    }
    
    private AIAnalysisResponse parseAnalysisResponse(String response) {
        AIAnalysisResponse analysis = new AIAnalysisResponse();
        String[] lines = response.split("\n");
        
        for (String line : lines) {
            if (line.startsWith("FEEDBACK:")) {
                analysis.setFeedback(line.substring("FEEDBACK:".length()).trim());
            } else if (line.startsWith("CLARITY_SCORE:")) {
                analysis.setClarityScore(parseScore(line.substring("CLARITY_SCORE:".length()).trim()));
            } else if (line.startsWith("CORRECTNESS_SCORE:")) {
                analysis.setCorrectnessScore(parseScore(line.substring("CORRECTNESS_SCORE:".length()).trim()));
            } else if (line.startsWith("CONFIDENCE_SCORE:")) {
                analysis.setConfidenceScore(parseScore(line.substring("CONFIDENCE_SCORE:".length()).trim()));
            } else if (line.startsWith("OVERALL_SCORE:")) {
                analysis.setOverallScore(parseScore(line.substring("OVERALL_SCORE:".length()).trim()));
            } else if (line.startsWith("REQUIRES_FOLLOW_UP:")) {
                analysis.setRequiresFollowUp(Boolean.parseBoolean(line.substring("REQUIRES_FOLLOW_UP:".length()).trim()));
            } else if (line.startsWith("SUGGESTED_FOLLOW_UP:")) {
                String followUp = line.substring("SUGGESTED_FOLLOW_UP:".length()).trim();
                analysis.setSuggestedFollowUp("null".equalsIgnoreCase(followUp) ? null : followUp);
            }
        }
        
        return analysis;
    }
    
    private Double parseScore(String scoreStr) {
        try {
            return Double.parseDouble(scoreStr);
        } catch (NumberFormatException e) {
            return 5.0; // Default score
        }
    }
    
    private AIAnalysisResponse createDefaultAnalysis() {
        AIAnalysisResponse analysis = new AIAnalysisResponse();
        analysis.setFeedback("Unable to analyze answer at this time. Please try again.");
        analysis.setClarityScore(5.0);
        analysis.setCorrectnessScore(5.0);
        analysis.setConfidenceScore(5.0);
        analysis.setOverallScore(5.0);
        analysis.setRequiresFollowUp(false);
        analysis.setSuggestedFollowUp(null);
        return analysis;
    }
    
    private void generateFollowUpQuestion(MockInterview interview, InterviewQuestion parentQuestion, String suggestedFollowUp) {
        InterviewQuestion followUp = new InterviewQuestion();
        followUp.setMockInterview(interview);
        followUp.setQuestion(suggestedFollowUp != null ? suggestedFollowUp : 
            "Can you elaborate more on your previous answer?");
        followUp.setDifficulty(parentQuestion.getDifficulty());
        followUp.setCategory(parentQuestion.getCategory());
        followUp.setQuestionOrder(parentQuestion.getQuestionOrder() + 1);
        followUp.setIsFollowUp(true);
        followUp.setParentQuestionId(parentQuestion.getId());
        
        questionRepository.save(followUp);
    }
    
    public InterviewResultDTO completeInterview(Long interviewId) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found"));
        return completeInterview(interview);
    }
    
    private InterviewResultDTO completeInterview(MockInterview interview) {
        interview.setStatus(MockInterview.InterviewStatus.COMPLETED);
        interview.setEndTime(LocalDateTime.now());
        
        // Calculate overall score
        List<InterviewQuestion> questions = questionRepository.findByMockInterviewIdOrderByQuestionOrder(interview.getId());
        Double overallScore = questions.stream()
            .filter(q -> q.getOverallScore() != null)
            .mapToDouble(InterviewQuestion::getOverallScore)
            .average()
            .orElse(0.0);
        
        interview.setOverallScore(overallScore);
        interview.setOverallFeedback(generateOverallFeedback(questions, overallScore));
        mockInterviewRepository.save(interview);
        
        return convertToResultDTO(interview, questions);
    }
    
    private String generateOverallFeedback(List<InterviewQuestion> questions, Double overallScore) {
        String prompt = String.format(
            "Based on these interview results with overall score %.1f/10, provide comprehensive feedback:\n%s\n\n" +
            "Focus on strengths and areas for improvement. Format as: STRENGTHS: [strengths] IMPROVEMENTS: [improvements]",
            overallScore,
            questions.stream()
                .map(q -> String.format("Q: %s | A: %s | Score: %.1f", 
                    q.getQuestion(), q.getUserAnswer(), q.getOverallScore()))
                .collect(Collectors.joining("\n"))
        );
        
        try {
            return groqAIService.generateContent(prompt);
        } catch (Exception e) {
            return "Good performance overall. Keep practicing to improve your skills.";
        }
    }
    
    private InterviewQuestionDTO convertToDTO(InterviewQuestion question) {
        InterviewQuestionDTO dto = new InterviewQuestionDTO();
        dto.setQuestionId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setDifficulty(question.getDifficulty());
        dto.setCategory(question.getCategory());
        dto.setQuestionOrder(question.getQuestionOrder());
        dto.setIsFollowUp(question.getIsFollowUp());
        return dto;
    }
    
    private InterviewResultDTO convertToResultDTO(MockInterview interview, List<InterviewQuestion> questions) {
        InterviewResultDTO result = new InterviewResultDTO();
        result.setInterviewId(interview.getId());
        result.setOverallScore(interview.getOverallScore());
        result.setOverallFeedback(interview.getOverallFeedback());
        result.setTotalQuestions(interview.getTotalQuestions());
        result.setQuestionsAnswered(interview.getQuestionsAnswered());
        
        List<QuestionResultDTO> questionResults = questions.stream()
            .map(this::convertToQuestionResultDTO)
            .collect(Collectors.toList());
        result.setQuestionResults(questionResults);
        
        // Extract strengths and improvements from feedback
        String[] feedbackParts = interview.getOverallFeedback().split("STRENGTHS:|IMPROVEMENTS:");
        if (feedbackParts.length >= 3) {
            result.setStrengths(feedbackParts[1].trim());
            result.setImprovements(feedbackParts[2].trim());
        }
        
        return result;
    }
    
    private QuestionResultDTO convertToQuestionResultDTO(InterviewQuestion question) {
        QuestionResultDTO dto = new QuestionResultDTO();
        dto.setQuestion(question.getQuestion());
        dto.setUserAnswer(question.getUserAnswer());
        dto.setFeedback(question.getAiFeedback());
        dto.setScore(question.getOverallScore());
        dto.setDifficulty(question.getDifficulty());
        return dto;
    }
    
    public List<MockInterview> getStudentInterviews(Long userId) {

         StudentDetails student = studentDetailsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException ("Student not found for user id: " + userId));
        return mockInterviewRepository.findByStudentIdOrderByCreatedAtDesc(student.getStudentId());
    }
    
    public List<MockInterview> getAllInterviews() {
        return mockInterviewRepository.findAll();
    }
}
