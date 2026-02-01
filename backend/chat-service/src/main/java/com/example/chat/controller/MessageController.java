package com.example.chat.controller;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.CreateMessageResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.publisher.AiPublisher;
import com.example.chat.service.ChatService;
import com.example.chat.service.SseService;
import com.example.chat.service.impl.MessageServiceImpl;
import com.example.common.jwt.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AiPublisher aiPublisher;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages(
            @PathVariable("chatId") UUID chatId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = (user != null) ? user.id() : null;
        List<MessageResponse> messages = chatService.getAllMessagesInChat(chatId,userId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<CreateMessageResponse> createMessage(
            @PathVariable("chatId") UUID chatId,
            @Valid @RequestBody CreateMessageRequest createMessageRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = (user != null) ? user.id() : null;

        CreateMessageResponse created = messageService.createMessage(createMessageRequest, chatId,userId);

        // emit new user message to SSE subscribers
        sseService.emit(chatId, ChatEvent.USER_MESSAGE, created.content());

        // generating response from AI service and emitting it asynchronously
//        aiService.processAiResponseAsync(chatId, created.content());
        aiPublisher.publishGenerateAiResponseEvent(chatId, created.content());

        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("chatId") UUID chatId,
            @PathVariable("messageId") UUID messageId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = (user != null) ? user.id() : null;

        messageService.deleteMessage(chatId, messageId,userId);
        return ResponseEntity.noContent().build();
    }
}
