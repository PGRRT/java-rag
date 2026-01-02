package com.example.chat.service;

import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.ChatResponse;
import com.example.chat.domain.dto.chat.response.ChatWithMessagesResponse;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.ChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatService {
    Page<ChatResponse> getGlobalAndUserChats(UUID userId, ChatType chatType, Pageable pageable);
    CreateChatResponse saveChat(CreateChatRequest chatRequest, UUID userId);

    List<MessageResponse> getAllMessagesInChat(UUID chatId, UUID userId);
    void deleteChat(UUID chatId,UUID userId);

    boolean existsById(UUID chatId);
    Chat findById(UUID chatId);
}
