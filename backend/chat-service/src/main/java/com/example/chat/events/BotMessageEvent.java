package com.example.chat.events;


import java.util.UUID;

public record BotMessageEvent(
        UUID chatId,
        String message
)  {
}
