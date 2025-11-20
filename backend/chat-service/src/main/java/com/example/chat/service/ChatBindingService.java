package com.example.chat.service;

import java.util.UUID;

public interface ChatBindingService {
    void bindChat(UUID chatId);
    void unBindChat(UUID chatId);
}
