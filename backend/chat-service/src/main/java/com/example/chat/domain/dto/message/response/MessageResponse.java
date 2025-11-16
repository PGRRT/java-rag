package com.example.chat.domain.dto.message.response;


import com.example.chat.domain.enums.Sender;

import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        Sender sender
) {
}