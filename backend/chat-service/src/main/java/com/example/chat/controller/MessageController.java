package com.example.chat.controller;

import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.CreateMessageResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.service.ChatService;
import com.example.chat.service.impl.MessageServiceImpl;
import com.example.common.jwt.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats/{chatId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageServiceImpl messageService;
    private final ChatService chatService;


    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getLastMessages(
            @PathVariable("chatId") UUID chatId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = user != null ? user.id() : null;

        Page<MessageResponse> messages = chatService.getMessagesInChat(chatId,userId, pageable);

        return ResponseEntity.ok(messages);
    }


    @PostMapping
    public ResponseEntity<CreateMessageResponse> createMessage(
            @PathVariable("chatId") UUID chatId,
            @Valid @RequestBody CreateMessageRequest createMessageRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = (user != null) ? user.id() : null;

        CreateMessageResponse created = messageService.saveUserMessage(createMessageRequest.content(), createMessageRequest.sender(), chatId,userId);

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
