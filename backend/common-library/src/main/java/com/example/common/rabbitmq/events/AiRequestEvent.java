package com.example.common.rabbitmq.events;


import java.util.UUID;

public record AiRequestEvent(
        UUID chatId,
        String content
)  {
}