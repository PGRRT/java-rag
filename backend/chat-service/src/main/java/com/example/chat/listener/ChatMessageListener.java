package com.example.chat.listener;


import com.example.chat.service.SseService;
import com.example.common.rabbitmq.events.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageListener {
    private final SseService sseService;
    private final Queue instanceQueue;

    @RabbitListener(queues = "#{instanceQueue.name}")
    public void onMessage(ChatMessageEvent event) {
        if (!sseService.hasEmitters(event.chatId())) {
            log.debug("No active SSE emitter for chatId {}, skipping message", event.chatId());
            return;
        }

        sseService.emit(event.chatId(), event.chatEvent(), event.message());
    }
}
