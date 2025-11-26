package com.example.chat.service.impl;

import com.example.chat.domain.dto.ai.response.AiResponse;
import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.domain.enums.Sender;
import com.example.chat.service.AiService;
import com.example.chat.service.MessageService;
import com.example.chat.service.SseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final RestTemplate restTemplate;
    private final MessageService messageService;
    private final SseService sseService;

    @Override
    public String generateResponse(UUID chatId, String prompt) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("query", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        AiResponse response = restTemplate.postForObject("http://api:9000/query/" + chatId, entity,AiResponse.class);
        if (response == null) {
            log.error("AI service returned null for chatId {}", chatId);
            throw new RuntimeException("AI service returned null");
        } else if (!response.success()) {
            log.error("AI service returned an error for chatId {}: {}", chatId, response.message());
            throw new RuntimeException("AI service error");
        }

        return response.message();
    }

    @Override
    @Async
    public void processAiResponseAsync(UUID chatId, String message) {
        try {
            String generatedResponse = generateResponse(chatId, message);
            messageService.saveBotMessage(chatId, generatedResponse);
        } catch (Exception ex) {
            log.error("Async AI processing failed for chat {}", chatId, ex);
            sseService.emit(chatId, ChatEvent.ERROR, "AI processing failed");
        }
    }
}
