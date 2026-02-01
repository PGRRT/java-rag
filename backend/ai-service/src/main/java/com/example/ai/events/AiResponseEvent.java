package com.example.ai.events;
import java.util.UUID;

public record AiResponseEvent(
        UUID chatId,
        String content
) {}
