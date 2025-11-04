package com.college.PlacementApl.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Choice {

    @JsonProperty("index")
    private int index;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("finish_reason")
    private String finishReason;

}
