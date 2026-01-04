package com.example.chat.service.impl;

import com.example.chat.domain.dto.ai.response.AiResponse;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final RestClient restClient;
    private final MessageService messageService;

    @Override
    public String generateResponse(UUID chatId, String prompt) {
        Map<String, String> requestBody = Map.of("query", prompt);

        AiResponse response = restClient.post().uri("http://api:9000/query/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    log.error("AI service error for chatId {}: status {}", chatId, res.getStatusCode());
                    throw new RuntimeException("AI service failed with status " + res.getStatusCode());
                })
                .body(AiResponse.class);

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
            String errorResponse = "I'm sorry, but I'm unable to process your request at the moment.";
            messageService.saveBotMessage(chatId, errorResponse);
        }
    }
}
