package com.example.chat.domain.dto.message.request;

import com.example.chat.domain.enums.Sender;

import java.util.UUID;

public record CreateMessageRequest(
        String content,
        Sender sender
//        UUID userId
) {
}
