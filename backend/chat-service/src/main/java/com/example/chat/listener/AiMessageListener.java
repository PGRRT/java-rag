//package com.example.chat.listener;
//
//import com.example.chat.config.RabbitMqConfig;
//import com.example.chat.events.ChatMessage;
//import com.example.chat.domain.dto.message.request.CreateMessageRequest;
//import com.example.chat.domain.enums.Sender;
//import com.example.chat.service.AiService;
//import com.example.chat.service.ChatMessageProcessor;
//import com.example.chat.service.ChatService;
//import com.example.chat.service.MessageService;
//import com.rabbitmq.client.Channel;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class AiMessageListener {
//    private final AiService aiService;
//    private final MessageService messageService;
//    private final ChatService chatService;
//    private final ChatMessageProcessor chatMessageProcessor;
//
//    @RabbitListener(queues = RabbitMqConfig.queueName, ackMode = "MANUAL")
//    public void handleAiMessage(ChatMessage chatMessage, Channel channel,
//                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
//        try {
//            log.info("Receive message from queue for chat={}", chatMessage.chatId());
//
//            chatMessageProcessor.processIncomingMessage(chatMessage);
//
//            channel.basicAck(tag, false);
//        } catch (Exception e) {
//            log.error("Error processing AI message for chat={}: {}", chatMessage.chatId(), e.getMessage());
//
//            // Nack the message and requeue it for later processing
//            channel.basicNack(tag, false, true);
//        }
//    }
//}
