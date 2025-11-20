package com.example.chat.service.impl;

import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.ChatResponse;
import com.example.chat.domain.dto.chat.response.ChatWithMessagesResponse;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.request.CreateMessageRequest;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.Sender;
import com.example.chat.mapper.ChatMapper;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.ChatRepository;
import com.example.chat.service.AiService;
import com.example.chat.service.ChatService;
import com.example.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    public List<ChatResponse> getAllChats() {

        // TODO:
        // in future we will get this from security context
        UUID userId = null;
        List<Chat> chatsForUser = chatRepository.findChatsForUser(userId);

        return chatsForUser.stream().map(
                chatMapper::toChatResponse
        ).toList();
    }

    public List<ChatWithMessagesResponse> getAllChatsWithMessages() {

        // TODO:
        // in future we will get this from security context
        UUID userId = null;
        List<Chat> chatsForUser = chatRepository.findChatsForUser(userId);

        return chatsForUser.stream().map(
                chatMapper::toChatWithMessagesResponse
        ).toList();
    }

    @Transactional
    public CreateChatResponse saveChat(CreateChatRequest chatRequest) {
        // check if user exists

        Chat chat = chatMapper.toEntity(chatRequest);
        Chat savedChat = chatRepository.save(chat);
        return chatMapper.toCreateChatResponse(savedChat);
    }


    public List<MessageResponse> getAllMessagesInChat(UUID chatId) {
        Chat chat = chatRepository.findChatWithMessagesById(chatId).orElseThrow(() -> {
            log.warn("Chat with id {} not found when fetching messages.", chatId);
            return new IllegalArgumentException("Chat with id " + chatId + " not found.");
        });

        return chat.getMessages().stream().map(messageMapper::toMessageResponse).toList();
    }

    @Transactional
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
