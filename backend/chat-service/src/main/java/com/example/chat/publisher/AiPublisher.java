package com.example.chat.publisher;

import com.example.chat.events.GenerateAiResponseEvent;
import com.example.common.SharedRabbitTopology;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishGenerateAiResponseEvent(UUID chatId, String content) {
        GenerateAiResponseEvent event = new GenerateAiResponseEvent(chatId, content);

        rabbitTemplate.convertAndSend(SharedRabbitTopology.AI_EXCHANGE, SharedRabbitTopology.GENERATE_ROUTING_KEY, event);
    }
}
