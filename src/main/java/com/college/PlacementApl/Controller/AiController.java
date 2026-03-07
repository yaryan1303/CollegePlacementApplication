package com.college.PlacementApl.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.college.PlacementApl.Service.AiChatService;
import com.college.PlacementApl.dtos.ChatRequest;
import com.college.PlacementApl.dtos.ChatResponse;
import com.college.PlacementApl.dtos.PlacementPredictionRequest;
import com.college.PlacementApl.dtos.PlacementPredictionResult;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    // ==================== FEATURE 3: AI CHATBOT ====================
    // POST /api/ai/chat
    // Body: { "message": "Which companies are visiting next week?", "userId": 5 }
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    // ==================== FEATURE 1: AI PLACEMENT PREDICTOR ====================
    // POST /api/ai/predict-placement
    // Body: { "cgpa": 8.5, "skills": ["java","spring","sql"], "department": "CSE", "batchYear": 2025 }
    @PostMapping("/predict-placement")
    public ResponseEntity<PlacementPredictionResult> predictPlacement(
            @RequestBody PlacementPredictionRequest request) {
        PlacementPredictionResult result = aiChatService.predictPlacement(request);
        return ResponseEntity.ok(result);
    }
}
