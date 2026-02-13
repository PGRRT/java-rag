package com.example.chat.service;

import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.enums.Sender;
import com.example.chat.repository.MessageRepository;
import com.example.common.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private final MessageRepository messageRepository;

    public void getChatHistory(GetMessagesRequest request, StreamObserver<MessagesResponse> responseObserver) {
        String chatId = request.getChatId();
        int maxResults = request.getMaxResults();

        Pageable pageable = PageRequest.of(0, maxResults, Sort.by("createdAt").descending());

        List<Message> messages = messageRepository.findByChatId(UUID.fromString(chatId), pageable)
                .map(this::mapToProto)
                .toList();

        MessagesResponse reply = MessagesResponse.newBuilder()
                .addAllMessages(messages)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    private Message mapToProto(com.example.chat.domain.entities.Message entity) {
        return Message.newBuilder()
                .setId(entity.getId().toString())
                .setContent(entity.getContent())
                .setSender(entity.getSender() == Sender.USER ? MessageSender.USER : MessageSender.BOT)
                .build();
    }
}
