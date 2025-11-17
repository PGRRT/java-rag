package com.example.chat.service.impl;

import com.example.chat.config.RabbitMqConfig;
import com.example.chat.domain.dto.ai.request.ChatMessage;
import com.example.chat.service.ChatMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessagePublisherImpl implements ChatMessagePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishMessage(UUID chatId, String message) {
        log.info("Publishing message to RabbitMQ: {}", message);
        ChatMessage chatMessage = new ChatMessage(chatId, message);
        rabbitTemplate.convertAndSend(RabbitMqConfig.topicExchangeName, RabbitMqConfig.ROUTING_KEY, chatMessage);

        // Implementation to publish chat message
    }
}
