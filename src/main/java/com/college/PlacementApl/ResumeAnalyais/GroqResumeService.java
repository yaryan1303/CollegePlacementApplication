package com.college.PlacementApl.ResumeAnalyais;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import com.college.PlacementApl.dtos.GroqRequest;
import com.college.PlacementApl.dtos.GroqResponse;
import com.college.PlacementApl.dtos.InterviewQA;
import com.college.PlacementApl.dtos.InterviewResponse;
import com.college.PlacementApl.dtos.Message;
import com.google.common.util.concurrent.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Slf4j
@Service
public class GroqResumeService {

    private final WebClient webClient;
    private final String model;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public GroqResumeService(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model) {
        this.model = model;
        this.rateLimiter = RateLimiter.create(3.0 / 60.0);
        this.objectMapper = new ObjectMapper();

        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String generateFeedback(String resumeText) {
        if (!rateLimiter.tryAcquire()) {
            return "AI feedback temporarily unavailable (rate limited)";
        }

        String truncatedText = resumeText.length() > 3000
                ? resumeText.substring(0, 3000)
                : resumeText;

        String prompt = """
                You are a professional career counselor. Provide exactly 4 concise, numbered suggestions
                to improve this resume. Focus on content, structure, and presentation. Be specific.
                Resume text: %s
                """.formatted(truncatedText);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful career advisor."),
                        Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 500);

        try {
            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .map(this::extractContentFromGroqResponse)
                    .onErrorReturn(
                            "Default feedback: 1. Check formatting 2. Review bullet points 3. Verify contact info 4. Tailor skills")
                    .block();
        } catch (Exception e) {
            return "AI feedback service is busy. Please try again later.";
        }
    }

    private String extractContentFromGroqResponse(String response) {
        try {
            if (response.contains("\"content\":")) {
                String content = response.split("\"content\":\"?")[1];
                if (content.startsWith("\"")) {
                    return content.substring(1, content.indexOf("\"", 1));
                } else {
                    int endIndex = Math.min(
                            content.indexOf(","),
                            content.indexOf("}"));
                    endIndex = endIndex == -1 ? content.length() : endIndex;
                    return content.substring(0, endIndex);
                }
            }
            return response;
        } catch (Exception e) {
            return "Could not parse AI response";
        }
    }

    public CompletableFuture<InterviewResponse> generateInterviewQuestions(String technology) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire();

            String prompt = String.format(
                    "Generate exactly 20 interview questions for %s technology. " +
                            "Format each exactly as: QUESTION: [number]. [question text] ANSWER: [detailed answer] DIFFICULTY: [Beginner/Intermediate/Expert]. "
                            +
                            "Ensure you generate exactly 20 questions without skipping any numbers.",
                    technology);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);
            requestBody.put("top_p", 1.0);
            requestBody.put("stream", false);

            try {
                log.info("=== GROQ API REQUEST ===");
                log.info("Technology: {}", technology);
                log.info("Model: {}", model);
                log.info("Prompt length: {}", prompt.length());

                // Log the actual request being sent
                String requestJson = objectMapper.writeValueAsString(requestBody);
                log.info("Request JSON: {}", requestJson);

                GroqResponse response = webClient.post()
                        .uri("/chat/completions")
                        .bodyValue(requestBody)
                        .retrieve()
                        .onStatus(status -> status.isError(), clientResponse -> {
                            log.error("=== GROQ API ERROR ===");
                            log.error("Status Code: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error Body: {}", errorBody);
                                        log.error("Headers: {}", clientResponse.headers().asHttpHeaders());
                                        return Mono.error(new RuntimeException("Groq API error - Status: "
                                                + clientResponse.statusCode() + ", Body: " + errorBody));
                                    });
                        })
                        .bodyToMono(GroqResponse.class)
                        .doOnSuccess(r -> {
                            if (r != null) {
                                log.info("=== GROQ API SUCCESS ===");
                                log.info("Response received successfully");
                                log.info("Choices count: {}", r.getChoices() != null ? r.getChoices().size() : 0);
                            }
                        })
                        .doOnError(e -> {
                            log.error("=== GROQ API EXCEPTION ===");
                            log.error("Exception type: {}", e.getClass().getSimpleName());
                            log.error("Exception message: {}", e.getMessage());
                        })
                        .block();

                if (response == null) {
                    log.warn("Response is null");
                    return createFallbackResponse(technology);
                }

                if (response.getChoices() == null || response.getChoices().isEmpty()) {
                    log.warn("No choices in response");
                    return createFallbackResponse(technology);
                }

                log.info("Successfully parsed response with {} choices", response.getChoices().size());
                return parseResponse(response, technology);

            } catch (Exception e) {
                log.error("=== COMPLETE FAILURE ===");
                log.error("Error generating questions for {}: {}", technology, e.getMessage(), e);
                return createFallbackResponse(technology);
            }
        });
    }

    public CompletableFuture<String> askFollowupQuestion(String technology, String context, String question) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire();

            String prompt = String.format(
                    "Technology: %s\nContext: %s\nQuestion: %s\nProvide a detailed technical answer:",
                    technology, context, question);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1024);
            requestBody.put("top_p", 1.0);

            try {
                GroqResponse response = webClient.post()
                        .uri("/chat/completions")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(GroqResponse.class)
                        .block();

                if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                    return response.getChoices().get(0).getMessage().getContent();
                }
                return "No response generated.";
            } catch (Exception e) {
                return "Error generating response: " + e.getMessage();
            }
        });
    }

    private InterviewResponse parseResponse(GroqResponse response, String technology) {
        String content = response.getChoices().get(0).getMessage().getContent();
        List<InterviewQA> qaList = parseQAFromContent(content);
        return new InterviewResponse(technology, qaList, content);
    }

    private List<InterviewQA> parseQAFromContent(String content) {
        List<InterviewQA> qaList = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return qaList;
        }

        log.info("Starting to parse content, length: {}", content.length());

        // Split by "QUESTION:" pattern (case insensitive)
        String[] sections = content.split("(?i)QUESTION:");

        log.info("Found {} sections after splitting", sections.length);

        for (int i = 1; i < sections.length && i <= 20; i++) {
            try {
                String section = sections[i].trim();
                if (section.isEmpty())
                    continue;

                log.debug("Processing section {}: {}", i, section.substring(0, Math.min(100, section.length())));

                // Extract question (everything until "ANSWER:")
                int answerIndex = section.indexOf("ANSWER:");
                if (answerIndex == -1) {
                    log.warn("No ANSWER found in section {}", i);
                    continue;
                }

                String question = section.substring(0, answerIndex).trim();

                // Extract answer (everything from "ANSWER:" until "DIFFICULTY:")
                int difficultyIndex = section.indexOf("DIFFICULTY:", answerIndex);
                if (difficultyIndex == -1) {
                    log.warn("No DIFFICULTY found in section {}", i);
                    continue;
                }

                String answer = section.substring(answerIndex + "ANSWER:".length(), difficultyIndex).trim();

                // Extract difficulty and the rest
                String remaining = section.substring(difficultyIndex + "DIFFICULTY:".length()).trim();
                String difficulty = remaining.split("\\n")[0].trim(); // Get first line after DIFFICULTY:

                log.info("Parsed - Q: {}, A: {} chars, D: {}",
                        question.length() > 50 ? question.substring(0, 50) + "..." : question,
                        answer.length(), difficulty);

                qaList.add(new InterviewQA(question, answer, difficulty));

            } catch (Exception e) {
                log.warn("Failed to parse question section {}: {}", i, e.getMessage());
            }
        }

        log.info("Successfully parsed {} questions", qaList.size());
        return qaList;
    }

    private InterviewResponse createFallbackResponse(String technology) {
        List<InterviewQA> questions = List.of(
                new InterviewQA("What are key features of " + technology + "?", "Key features include...", "Beginner"),
                new InterviewQA("Explain " + technology + " architecture", "Architecture consists of...",
                        "Intermediate"));
        return new InterviewResponse(technology, questions, "Fallback content");
    }

    private String extractValue(String line, String prefix) {
        return line.startsWith(prefix) ? line.substring(prefix.length()).trim() : line.trim();
    }
}