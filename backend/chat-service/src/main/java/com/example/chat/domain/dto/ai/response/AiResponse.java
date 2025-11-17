package com.example.chat.domain.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiResponse(
        boolean success,
        String message, // json
        List<String> contexts
) {
}
