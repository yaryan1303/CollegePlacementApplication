package com.college.PlacementApl.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GroqRequest {
     @JsonProperty("model")
        private String model;
        
        @JsonProperty("messages")
        private List<Message> messages;
        
        @JsonProperty("temperature")
        private double temperature;
        
        @JsonProperty("max_tokens")
        private int maxTokens;
        
        @JsonProperty("top_p")
        private double topP;
        
        public GroqRequest(String model, List<Message> messages, double temperature, int maxTokens, double topP) {
            this.model = model;
            this.messages = messages;
            this.temperature = temperature;
            this.maxTokens = maxTokens;
            this.topP = topP;
        }

}
