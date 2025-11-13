package com.example.chat.service.impl;

import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.entities.Message;
import com.example.chat.mapper.ChatMapper;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.ChatRepository;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public CreateChatResponse saveChat(CreateChatRequest chatRequest) {
        // check if user exists

        Chat chat = chatMapper.toEntity(chatRequest);
        Chat savedChat = chatRepository.save(chat);
        return chatMapper.toResponse(savedChat);
    }

    public List<MessageResponse> getAllMessagesInChat(UUID chatId) {
        Chat chat = chatRepository.findChatWithMessagesById(chatId).orElseThrow(() -> {
            log.warn("Chat with id {} not found when fetching messages.", chatId);
            return new IllegalArgumentException("Chat with id " + chatId + " not found.");
        });

        return chat.getMessages().stream().map(messageMapper::toResponse).toList();
    }

    public void deleteChat(UUID chatId) {
        if (!chatRepository.existsById(chatId)) {
            log.warn("Chat with id {} not found for deletion.", chatId);
            throw new IllegalArgumentException("Chat with id " + chatId + " not found for deletion.");
        }
        chatRepository.deleteById(chatId);
    }

    public boolean existsById(UUID chatId) {
        return chatRepository.existsById(chatId);
    }

    public Chat findById(UUID chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> {
            log.warn("Chat with id {} not found.", chatId);
            return new IllegalArgumentException("Chat with id " + chatId + " not found.");
        });
    }

}
