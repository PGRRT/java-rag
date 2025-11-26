package com.example.chat.listener;


import com.example.chat.config.RabbitMqConfig;
import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.events.BotMessageEvent;
import com.example.chat.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageListener {
    private final SseService sseService;

    @RabbitListener(queues = RabbitMqConfig.PRIVATE_QUEUE)
    public void onMessage(BotMessageEvent event) {
        if (!sseService.hasEmitters(event.chatId())) {
            log.debug("No active SSE emitter for chatId {}, skipping message", event.chatId());
            return;
        }

        sseService.emit(event.chatId(), ChatEvent.BOT_MESSAGE, event.message());
    }
}
