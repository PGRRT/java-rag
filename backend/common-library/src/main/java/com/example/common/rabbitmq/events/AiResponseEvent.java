package com.example.common.rabbitmq.events;
import java.util.UUID;

public record AiResponseEvent(
        UUID chatId,
        String content
) {}
