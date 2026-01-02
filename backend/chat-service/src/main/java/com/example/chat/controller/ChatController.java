package com.example.chat.controller;


import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.ChatResponse;
import com.example.chat.domain.dto.chat.response.ChatWithMessagesResponse;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.ChatType;
import com.example.chat.service.ChatService;
import com.example.chat.service.SseService;
import com.example.chat.service.impl.ChatServiceImpl;
import com.example.common.jwt.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SseService sseService;

    @GetMapping(value = "/{chatId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@PathVariable("chatId") UUID chatId) {
        return sseService.createEmitter(chatId);
    }

    @GetMapping
    public ResponseEntity<Page<ChatResponse>> getAllChats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) ChatType type,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = user != null ? user.id() : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<ChatResponse> allChatsWithMessages = chatService.getGlobalAndUserChats(userId, type, pageable);

        return new ResponseEntity<>(allChatsWithMessages, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreateChatResponse> createChat(@Valid @RequestBody CreateChatRequest request,
                                                         @AuthenticationPrincipal UserPrincipal user
    ) {
        UUID userId = user != null ? user.id() : null;

        CreateChatResponse createChatResponse = chatService.saveChat(request, userId);
        return new ResponseEntity<>(createChatResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId,
                                           @AuthenticationPrincipal UserPrincipal user
                                           ) {
        UUID userId = user != null ? user.id() : null;

        chatService.deleteChat(chatId,userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
