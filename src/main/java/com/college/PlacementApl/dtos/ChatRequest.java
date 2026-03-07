package com.college.PlacementApl.dtos;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private Long userId;
}
