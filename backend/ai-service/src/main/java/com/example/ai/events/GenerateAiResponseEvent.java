package com.example.ai.events;


import java.util.UUID;

public record GenerateAiResponseEvent(
        UUID chatId,
        String content
)  {
}