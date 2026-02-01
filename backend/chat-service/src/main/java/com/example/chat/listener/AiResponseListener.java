package com.example.chat.listener;

import com.example.chat.service.MessageService;
import com.example.common.rabbitmq.SharedRabbitTopology;
import com.example.common.rabbitmq.events.AiResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiResponseListener {
    private final MessageService messageService;

    @RabbitListener(queues = SharedRabbitTopology.QUEUE_RESPONSE_NAME)
    public void handleAiResponse(AiResponseEvent event) {
        log.info("Received AI response for chat: {}", event.chatId());

        try {
            messageService.saveBotMessage(event.chatId(), event.content());

            log.info("Successfully saved and emitted AI message for chat: {}", event.chatId());
        } catch (Exception e) {
            log.error("Error processing AI response event for chat: {}", event.chatId(), e);
        }
    }
}
