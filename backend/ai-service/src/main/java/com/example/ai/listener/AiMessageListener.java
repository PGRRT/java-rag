package com.example.ai.listener;

import com.example.ai.config.RabbitMqConfig;
import com.example.ai.domain.dto.ai.response.AiResponse;
import com.example.ai.events.AiResponseEvent;
import com.example.ai.events.GenerateAiResponseEvent;
import com.example.common.SharedRabbitTopology;
import com.example.common.grpc.ChatServiceGrpc;
import com.example.common.grpc.GetMessagesRequest;
import com.example.common.grpc.MessagesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMessageListener {
    private final ChatServiceGrpc.ChatServiceBlockingStub chatStub;
    private final RabbitTemplate rabbitTemplate;
    private final RestClient restClient;

    @RabbitListener(queues = SharedRabbitTopology.QUEUE_GENERATE_NAME)
    public void handleAiRequest(GenerateAiResponseEvent generateAiResponseEvent) {
        UUID chatId = generateAiResponseEvent.chatId();
        String prompt = generateAiResponseEvent.content();

        try {
            var historyRequest = GetMessagesRequest.newBuilder()
                    .setChatId(chatId.toString())
                    .setMaxResults(10)
                    .build();

            // fetch 10 most recent messages from chat service to provide context
            MessagesResponse chatHistory = chatStub.getChatHistory(historyRequest);

            List<String> lastMessages = chatHistory.getMessagesList().stream()
                    .map(msg -> msg.getSender() + ": " + msg.getContent())
                    .toList();

            Map<String, Object> requestBody = Map.of("query", prompt, "message_history", lastMessages);

            String aiAnswer = callAiApi(requestBody, chatId);

            rabbitTemplate.convertAndSend(SharedRabbitTopology.AI_EXCHANGE,SharedRabbitTopology.RESPONSE_ROUTING_KEY ,
                    new AiResponseEvent(chatId, aiAnswer));

        } catch (Exception ex) {
            log.error("AI processing failed for chat {}", chatId, ex);

            rabbitTemplate.convertAndSend(SharedRabbitTopology.AI_EXCHANGE, SharedRabbitTopology.RESPONSE_ROUTING_KEY,
                    new AiResponseEvent(chatId, "Sorry, I couldn't process your request at the moment."));
        }
    }

    private String callAiApi(Map<String, Object> requestBody, UUID chatId) {
        AiResponse response = restClient.post().uri("http://api:9000/query/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    log.error("AI service error for chatId {}: status {}", chatId, res.getStatusCode());
                    throw new RuntimeException("AI service failed with status " + res.getStatusCode());
                })
                .body(AiResponse.class);

        return (response != null && response.success()) ? response.message() : "Error";
    }
}
