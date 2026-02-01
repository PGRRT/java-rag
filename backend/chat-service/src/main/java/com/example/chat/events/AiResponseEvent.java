package com.example.chat.events;
import java.util.UUID;

public record AiResponseEvent(
        UUID chatId,
        String content
) {}
