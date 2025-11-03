package com.example.medai.controllers;


import com.example.medai.domain.dto.chat.request.CreateChatRequest;
import com.example.medai.domain.dto.chat.response.CreateChatResponse;
import com.example.medai.domain.entities.Chat;
import com.example.medai.mappers.ChatMapper;
import com.example.medai.services.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatResponse> createChat(@Valid @RequestBody CreateChatRequest request) {
        CreateChatResponse createChatResponse = chatService.saveChat(request);
        return new ResponseEntity<>(createChatResponse, HttpStatus.CREATED);
    }

//    @PostMapping("{id}")
//    public ResponseEntity<> readChat(@PathVariable String id) {
//        return "chatResponse";
//    }
}
