package com.example.ai.domain.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record AiResponse(
        @JsonProperty("user_question") String userQuestion,
        @JsonProperty("final_response") String finalResponse,
        @JsonProperty("total_steps") int totalSteps,
        @JsonProperty("thoughts_history") String thoughtsHistory,
        @JsonProperty("messages_history") List<Map<String, Object>> messagesHistory
) {
    public boolean success() {
        return finalResponse != null;
    }

    public String message() {
        return finalResponse;
    }
}
