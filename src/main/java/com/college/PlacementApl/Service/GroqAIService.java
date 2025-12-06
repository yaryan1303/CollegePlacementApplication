package com.college.PlacementApl.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroqAIService {
    
    private final WebClient webClient;
    private final String model;
    private final RateLimiter rateLimiter;

    // Use constructor injection instead of field injection
    public GroqAIService(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model) {
        
        this.model = model;
        this.rateLimiter = RateLimiter.create(5.0/60.0); // 5 requests per minute
        
        // Build WebClient with the API key
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        log.info("GroqAIService initialized with model: {}", model);
    }
    
    public String generateContent(String prompt) {
        rateLimiter.acquire(); // Rate limiting
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        
        try {
            Map<String, Object> response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
            
            return extractContentFromResponse(response);
        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage());
            throw new RuntimeException("AI service unavailable");
        }
    }
    
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
            throw new RuntimeException("Empty response from AI");
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse AI response");
        }
    }
}