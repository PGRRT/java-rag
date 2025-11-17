package com.example.chat.domain.dto.ai.request;


import java.io.Serializable;
import java.util.UUID;

public record ChatMessage  (
        UUID chatId,
        String content
)  {
}
