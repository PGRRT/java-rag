package com.example.common.rabbitmq.events;

import com.example.common.rabbitmq.enums.ChatEvent;
import com.example.common.rabbitmq.enums.Sender;

import java.util.UUID;

public record ChatMessageEvent(
        UUID chatId,
        String message,
        ChatEvent chatEvent
)  {
}
