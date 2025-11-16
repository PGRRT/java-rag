package com.example.chat.domain.dto.chat.response;


import java.util.UUID;



public record CreateChatResponse (
        UUID id,
        String title
){}