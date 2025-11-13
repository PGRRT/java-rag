package com.example.chat.service;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.MessageResponse;
import java.util.UUID;

public interface MessageService {
    MessageResponse getMessageById(UUID chatId, UUID messageId);
    MessageResponse createMessage(UUID chatId, CreateMessageRequest createMessageRequest);
    void deleteMessage(UUID chatId, UUID messageId);

}
