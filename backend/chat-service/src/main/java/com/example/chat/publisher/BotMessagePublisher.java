package com.example.chat.publisher;

import com.example.chat.events.BotMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.chat.config.RabbitMqConfig.TOPIC_EXCHANGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(BotMessageEvent event) {
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, "chat." + event.chatId(), event);
        log.debug("Published bot message for chatId {}", event.chatId());
    }

}
