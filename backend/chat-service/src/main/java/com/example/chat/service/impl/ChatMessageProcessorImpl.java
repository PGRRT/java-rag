//package com.example.chat.service.impl;
//
//import com.example.chat.events.ChatMessage;
//import com.example.chat.domain.dto.message.request.CreateMessageRequest;
//import com.example.chat.domain.enums.Sender;
//import com.example.chat.service.AiService;
//import com.example.chat.service.ChatMessageProcessor;
//import com.example.chat.service.MessageService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatMessageProcessorImpl implements ChatMessageProcessor {
//
//    private final AiService aiService;
//    private final MessageService messageService;
//
//    public void processIncomingMessage(ChatMessage chatMessage) {
//        log.info("Received chat message from RabbitMQ: {}", chatMessage);
//        UUID chatId = chatMessage.chatId();
//
//        String generatedResponse = aiService.generateResponse(chatMessage.chatId(), chatMessage.content());
//        messageService.createMessage(chatId, new CreateMessageRequest(generatedResponse, Sender.BOT));
//    }
//}
