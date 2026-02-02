package com.example.chat.service.impl;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.CreateMessageResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.entities.Message;
import com.example.chat.domain.enums.ChatType;
import com.example.chat.domain.enums.Sender;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.MessageRepository;
import com.example.chat.service.MessageService;
import com.example.common.rabbitmq.events.BotMessageEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChatRepository chatRepository;

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(UUID chatId, UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> {
            log.error("Message with id {} not found in chat {}", messageId, chatId);
            throw new IllegalArgumentException("Message not found");
        });

        if (!message.getChat().getId().equals(chatId)) {
            log.warn("Consistency breach: Message {} requested via wrong chat {}", messageId, chatId);
            throw new EntityNotFoundException("Message not found");
        }

        return messageMapper.toMessageResponse(message);
    }

    @Override
    @Transactional
    public CreateMessageResponse createMessage(CreateMessageRequest createMessageRequest, UUID chatId, UUID userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        boolean isGlobal = chat.getChatType() == ChatType.GLOBAL;
        boolean isOwner = chat.getUserId() != null && chat.getUserId().equals(userId);
        boolean isBot = createMessageRequest.sender() == Sender.BOT;

        if (!isGlobal && !isOwner && !isBot) {
            log.error("User {} is not authorized to add messages to chat {}", userId, chatId);
            throw new EntityNotFoundException("Chat not found");
        }

        Message message = messageMapper.toEntity(createMessageRequest);

        message.setChat(chat);
        message.setUserId(userId);

        Message save = messageRepository.save(message);

        return messageMapper.toCreateMessageResponse(save);
    }

    @Override
    @Transactional
    public void saveBotMessage(UUID chatId, String generatedResponse) {
        createMessage(new CreateMessageRequest(generatedResponse, Sender.BOT), chatId, null);

        // Publish event to notify SSE listeners
        applicationEventPublisher.publishEvent(new BotMessageEvent(chatId, generatedResponse));
    }

    @Override
    @Transactional
    public void deleteMessage(UUID chatId, UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> {
            log.warn("Message with id {} not found in chat {}", messageId, chatId);
            return new EntityNotFoundException("Message not found");
        });

        Chat chat = message.getChat();

        if (!message.getChat().getId().equals(chatId)) {
            log.warn("Message {} does not belong to chat {}", messageId, chatId);
            throw new EntityNotFoundException("Message not found");
        }

        boolean isPrivate = chat.getChatType() == ChatType.PRIVATE;

        if (isPrivate) {
            boolean isAuthor = message.getUserId() != null && message.getUserId().equals(userId);
            boolean isChatOwner = chat.getUserId() != null && chat.getUserId().equals(userId);
            if (!isAuthor && !isChatOwner) {
                log.warn("User {} is not authorized to delete message {} in chat {}", userId, messageId, chatId);
                throw new EntityNotFoundException("Not authorized to delete this message");
            }
        }

        messageRepository.delete(message);
    }
}

