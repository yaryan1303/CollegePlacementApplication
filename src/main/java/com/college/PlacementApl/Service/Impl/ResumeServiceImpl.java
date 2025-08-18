package com.college.PlacementApl.Service.Impl;

import com.college.PlacementApl.Service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    private final String groqApiUrl;
    private final String groqApiKey;

    public ResumeServiceImpl(
            RestTemplate restTemplate,
            @Value("${groq.api.url}") String groqApiUrl,
            @Value("${groq.api.key}") String groqApiKey) {
        this.restTemplate = restTemplate;
        this.groqApiUrl = groqApiUrl;
        this.groqApiKey = groqApiKey;
    }

    @Override
    public Map<String, Object> generateResumeResponse(String userResumeDescription) throws IOException {
        String promptString = loadPromptFromFile("resume_prompt.txt");
        String promptContent = putValuesToTemplate(promptString, Map.of(
                "userDescription", userResumeDescription));

        Map<String, Object> request = createGroqRequest(promptContent);
        HttpEntity<Map<String, Object>> entity = createRequestEntity(request);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        groqApiUrl,
                        HttpMethod.POST,
                        entity,
                        Map.class);
                return processGroqResponse(response.getBody());
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    throw new RuntimeException(
                            "Failed to get response from Groq API after " + MAX_RETRIES + " attempts", e);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        throw new RuntimeException("Unexpected error in Groq API communication");
    }

    private Map<String, Object> createGroqRequest(String promptContent) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "meta-llama/llama-4-scout-17b-16e-instruct"); // or "llama3-70b-8192"
        request.put("messages", List.of(
                Map.of("role", "user", "content", promptContent)));
        request.put("temperature", 0.7);
        request.put("max_tokens", 2000);
        return request;
    }

    private HttpEntity<Map<String, Object>> createRequestEntity(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);
        return new HttpEntity<>(request, headers);
    }

    private Map<String, Object> processGroqResponse(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("Invalid response from Groq API");
        }

        List<?> choices = (List<?>) responseBody.get("choices");
        if (choices.isEmpty()) {
            throw new RuntimeException("No choices in Groq API response");
        }

        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        String generatedText = (String) message.get("content");

        return parseMultipleResponses(generatedText);
    }

    // String loadPromptFromFile(String filename) throws IOException {
    // Path path = new ClassPathResource(filename).getFile().toPath();
    // return Files.readString(path);
    // }

    private String loadPromptFromFile(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + filename + " from classpath", e);
        }
    }

    String putValuesToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    public static Map<String, Object> parseMultipleResponses(String response) {
        Map<String, Object> jsonResponse = new HashMap<>();

        // Extract content inside <think> tags
        int thinkStart = response.indexOf("<think>") + 7;
        int thinkEnd = response.indexOf("</think>");
        if (thinkStart >= 7 && thinkEnd > thinkStart) {
            String thinkContent = response.substring(thinkStart, thinkEnd).trim();
            jsonResponse.put("think", thinkContent);
        } else {
            jsonResponse.put("think", null);
        }

        // Extract content that is in JSON format
        int jsonStart = response.indexOf("```json") + 7;
        int jsonEnd = response.lastIndexOf("```");
        if (jsonStart >= 7 && jsonEnd > jsonStart) {
            String jsonContent = response.substring(jsonStart, jsonEnd).trim();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> dataContent = objectMapper.readValue(jsonContent, Map.class);
                jsonResponse.put("data", dataContent);
            } catch (Exception e) {
                jsonResponse.put("data", null);
                throw new RuntimeException("Failed to parse JSON response", e);
            }
        } else {
            jsonResponse.put("data", null);
        }

        return jsonResponse;
    }
}
