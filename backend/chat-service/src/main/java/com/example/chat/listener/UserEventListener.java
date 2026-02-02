package com.example.chat.listener;

import com.example.chat.service.ChatService;
import com.example.common.rabbitmq.SharedRabbitTopology;
import com.example.common.rabbitmq.events.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final ChatService chatService;

    @RabbitListener(queues = SharedRabbitTopology.USER_DELETED_QUEUE)
    public void onUserDeleted(UserDeletedEvent event) {
        chatService.deleteAllChatsByUserId(event.userId());
    }
}
