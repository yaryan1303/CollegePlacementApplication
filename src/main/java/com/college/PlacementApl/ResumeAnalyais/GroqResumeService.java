package com.college.PlacementApl.ResumeAnalyais;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.common.util.concurrent.RateLimiter;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.Map;
import java.util.List;

@Service
public class GroqResumeService {

    private final WebClient webClient;
    private final String model;
    private final RateLimiter rateLimiter;

    public GroqResumeService(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.model:llama3-70b-8192}") String model) {
        this.model = model;
        this.rateLimiter = RateLimiter.create(3.0/60.0); // 3 requests per minute
        
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
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 500
        );

        try {
            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .map(this::extractContentFromGroqResponse)
                    .onErrorReturn("Default feedback: 1. Check formatting 2. Review bullet points 3. Verify contact info 4. Tailor skills")
                    .block();
        } catch (Exception e) {
            return "AI feedback service is busy. Please try again later.";
        }
    }

    private String extractContentFromGroqResponse(String response) {
        try {
            // Groq API response parsing
            if (response.contains("\"content\":")) {
                // Extract the content between "content": and the next comma or closing brace
                String content = response.split("\"content\":\"?")[1];
                if (content.startsWith("\"")) {
                    // If content is quoted
                    return content.substring(1, content.indexOf("\"", 1));
                } else {
                    // If content is not quoted
                    int endIndex = Math.min(
                        content.indexOf(","),
                        content.indexOf("}")
                    );
                    endIndex = endIndex == -1 ? content.length() : endIndex;
                    return content.substring(0, endIndex);
                }
            }
            return response;
        } catch (Exception e) {
            return "Could not parse AI response";
        }
    }
}
