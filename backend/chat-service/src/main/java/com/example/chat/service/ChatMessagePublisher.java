package com.example.chat.service;

import java.util.UUID;

public interface ChatMessagePublisher {
    void publishMessage(UUID chatId, String message);
}
