package com.example.chat.service;

import java.util.UUID;

public interface AiService {
    String generateResponse(int chatId, String prompt);
}
