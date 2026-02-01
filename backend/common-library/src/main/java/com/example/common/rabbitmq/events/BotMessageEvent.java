package com.example.common.rabbitmq.events;


import java.util.UUID;

public record BotMessageEvent(
        UUID chatId,
        String message
)  {
}
