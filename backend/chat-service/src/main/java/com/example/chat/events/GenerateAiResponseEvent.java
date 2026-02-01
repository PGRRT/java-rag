package com.example.chat.events;


import java.util.UUID;

public record GenerateAiResponseEvent(
        UUID chatId,
        String content
)  {
}