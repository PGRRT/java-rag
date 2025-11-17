package com.example.chat.service;

import com.example.chat.domain.dto.ai.request.ChatMessage;

public interface ChatMessageProcessor {
    void processIncomingMessage(ChatMessage chatMessage);
}
