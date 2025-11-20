package com.example.chat.service.impl;

import com.example.chat.service.ChatBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBindingServiceImpl implements ChatBindingService {
    private final AmqpAdmin amqpAdmin;
    private final Queue instanceQueue;
    private final TopicExchange topicExchange;

    public void bindChat(UUID chatId) {
        String routingKey = "chat." + chatId;
        Binding binding = BindingBuilder.bind(instanceQueue)
                .to(topicExchange)
                .with(routingKey); // routing key = chat.{chatId}
        try {
            amqpAdmin.declareBinding(binding);
            log.debug("Declared binding for {}", routingKey);
        } catch (Exception e) {
            log.error("Failed to declare binding for {}", routingKey, e);
            throw e;
        }
    }

    public void unBindChat(UUID chatId) {
        String routingKey = "chat." + chatId;
        Binding binding = BindingBuilder.bind(instanceQueue).to(topicExchange).with(routingKey);
        try {
            amqpAdmin.removeBinding(binding);
            log.debug("Removed binding for {}", routingKey);
        } catch (Exception e) {
            log.error("Failed to remove binding for {}", routingKey, e);
        }
    }

}
