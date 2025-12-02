package com.example.chat.controller;

//import com.example.chat.config.RabbitMqConfig;
import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.CreateMessageResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.service.AiService;
import com.example.chat.service.ChatService;
import com.example.chat.service.SseService;
import com.example.chat.service.impl.MessageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageServiceImpl messageService;
    private final ChatService chatService;
    private final SseService sseService;
    private final AiService aiService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages(
            @PathVariable("chatId") UUID chatId
    ) {
        List<MessageResponse> messages = chatService.getAllMessagesInChat(chatId);
        return ResponseEntity.ok(messages);
    }

//    @GetMapping("/{messageId}")
//    public ResponseEntity<MessageResponse> getMessageById(
//            @PathVariable("chatId") UUID chatId,
//            @PathVariable("messageId") UUID messageId
//    ) {
//        MessageResponse message = messageService.getMessageById(chatId, messageId);
//        return ResponseEntity.ok(message);
//    }



    @PostMapping
    public ResponseEntity<CreateMessageResponse> createMessage(
            @PathVariable("chatId") UUID chatId,
            @Valid @RequestBody CreateMessageRequest createMessageRequest
    ) {
        CreateMessageResponse created = messageService.createMessage(chatId, createMessageRequest);

        // emit new user message to SSE subscribers
        sseService.emit(chatId, ChatEvent.USER_MESSAGE, created.content());

        // generating response from AI service and emitting it asynchronously
        aiService.processAiResponseAsync(chatId, created.content());
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("chatId") UUID chatId,
            @PathVariable("messageId") UUID messageId
    ) {
        messageService.deleteMessage(chatId, messageId);
        return ResponseEntity.noContent().build();
    }
}
