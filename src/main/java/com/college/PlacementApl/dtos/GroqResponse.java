package com.college.PlacementApl.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GroqResponse {
    @JsonProperty("choices")
        private List<Choice> choices;
        
        public List<Choice> getChoices() {
            return choices;
        }

}
