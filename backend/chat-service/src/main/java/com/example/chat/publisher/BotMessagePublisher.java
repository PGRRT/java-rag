package com.example.chat.publisher;

import com.example.common.rabbitmq.SharedRabbitTopology;
import com.example.common.rabbitmq.events.BotMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class BotMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(BotMessageEvent event) {
        log.debug("Published bot message for chatId {}", event.chatId());
        rabbitTemplate.convertAndSend(SharedRabbitTopology.TOPIC_EXCHANGE, "chat." + event.chatId(), event);
    }

}
