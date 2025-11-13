package com.example.chat.domain.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;



public record CreateChatResponse (
        UUID id
){}