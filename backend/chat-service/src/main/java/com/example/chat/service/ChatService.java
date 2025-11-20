package com.example.chat.service;

import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.ChatResponse;
import com.example.chat.domain.dto.chat.response.ChatWithMessagesResponse;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatService {
    List<ChatWithMessagesResponse> getAllChatsWithMessages();
    List<ChatResponse> getAllChats();
    CreateChatResponse saveChat(CreateChatRequest chatRequest);

    List<MessageResponse> getAllMessagesInChat(UUID chatId);
    void deleteChat(UUID chatId);

    boolean existsById(UUID chatId);
    Chat findById(UUID chatId);
}
