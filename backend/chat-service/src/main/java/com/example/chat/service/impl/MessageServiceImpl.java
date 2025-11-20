package com.example.chat.service.impl;

//import com.example.chat.config.RabbitMqConfig;
import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.CreateMessageResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.entities.Message;
import com.example.chat.domain.enums.Sender;
import com.example.chat.events.BotMessageEvent;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.MessageRepository;
import com.example.chat.service.ChatService;
import com.example.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final MessageMapper messageMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RabbitTemplate rabbitTemplate;

    public MessageResponse getMessageById(UUID chatId, UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> {
            log.error("Message with id {} not found in chat {}", messageId, chatId);
            throw new IllegalArgumentException("Message not found");
        });
        
        return messageMapper.toMessageResponse(message);
    }

    @Transactional
    public CreateMessageResponse createMessage(UUID chatId, CreateMessageRequest createMessageRequest) {
        Chat chat = chatService.findById(chatId);

        Message message = messageMapper.toEntity(createMessageRequest);
        message.setChat(chat);
        Message save = messageRepository.save(message);

        return messageMapper.toCreateMessageResponse(save);
    }

    @Transactional
    public void saveBotMessage(UUID chatId, String generatedResponse) {
        createMessage(chatId, new CreateMessageRequest(generatedResponse, Sender.BOT));

        // Publish event to notify SSE listeners
        applicationEventPublisher.publishEvent(new BotMessageEvent(chatId, generatedResponse));
    }



    @Transactional
    public void deleteMessage(UUID chatId, UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> {
            log.error("Message with id {} not found in chat {}", messageId, chatId);
            return new IllegalArgumentException("Message not found");
        });

        if (!message.getChat().getId().equals(chatId)) {
            throw new IllegalArgumentException("Message does not belong to this chat");
        }

        messageRepository.deleteById(messageId);
    }
}

