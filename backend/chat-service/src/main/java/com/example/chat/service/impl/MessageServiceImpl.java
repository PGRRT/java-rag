package com.example.chat.service.impl;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.entities.Message;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.MessageRepository;
import com.example.chat.service.ChatService;
import com.example.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final MessageMapper messageMapper;

    public MessageResponse getMessageById(UUID chatId, UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> {
            log.error("Message with id {} not found in chat {}", messageId, chatId);
            throw new IllegalArgumentException("Message not found");
        });
        
        return messageMapper.toResponse(message);
    }

    public MessageResponse createMessage(UUID chatId, CreateMessageRequest createMessageRequest) {
        Chat chat = chatService.findById(chatId);

        Message message = messageMapper.toEntity(createMessageRequest);
        message.setChat(chat);
        Message save = messageRepository.save(message);
        return messageMapper.toResponse(save);
    }

    public void deleteMessage(UUID chatId, UUID messageId) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isEmpty()) {
            log.error("Message with id {} not found in chat {}", messageId, chatId);
            throw new IllegalArgumentException("Message not found");
        }
        messageRepository.deleteById(messageId);
    }


}

