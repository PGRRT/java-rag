package com.example.chat.service.impl;

import com.example.chat.domain.dto.chat.request.CreateChatRequest;
import com.example.chat.domain.dto.chat.response.ChatResponse;
import com.example.chat.domain.dto.chat.response.ChatWithMessagesResponse;
import com.example.chat.domain.dto.chat.response.CreateChatResponse;
import com.example.chat.domain.dto.message.response.MessageResponse;
import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.entities.Message;
import com.example.chat.domain.enums.ChatType;
import com.example.chat.domain.enums.Sender;
import com.example.chat.mapper.ChatMapper;
import com.example.chat.mapper.MessageMapper;
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.specificiation.ChatSpecification;
//import com.example.chat.service.AiService;
import com.example.chat.service.ChatService;
import com.example.chat.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ChatResponse> getGlobalAndUserChats(UUID userId, ChatType chatType, Pageable pageable) {
        Page<Chat> chatsPage = chatRepository.findAll(ChatSpecification.userChatsWithType(userId, chatType), pageable);

        return chatsPage.map(chatMapper::toChatResponse);
    }

    @Override
    @Transactional
    public CreateChatResponse saveChat(CreateChatRequest chatRequest,UUID userId) {
        Chat chat = chatMapper.toEntity(chatRequest);

        chat.setUserId(userId);

        Chat savedChat = chatRepository.save(chat);
        return chatMapper.toCreateChatResponse(savedChat);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessagesInChat(UUID chatId, UUID userId, Pageable pageable) {
        Chat chat = chatRepository.findById((chatId)).orElseThrow(() -> {
            log.warn("Chat with id {} not found when fetching messages.", chatId);
            return new EntityNotFoundException("Chat not found");
        });

        boolean isOwner = chat.getUserId() != null && chat.getUserId().equals(userId);
        boolean isGlobal = chat.getChatType() == ChatType.GLOBAL;

        if (!isGlobal && !isOwner) {
            log.warn("User {} tried to access messages of chat {} belonging to {}", userId, chatId, chat.getUserId());

            throw new EntityNotFoundException("Chat not found");
        }

        Page<Message> messages = messageRepository.findByChatId(chatId, pageable);

        return messages.map(messageMapper::toMessageResponse);
    }

    @Override
    @Transactional
    public void deleteAllChatsByUserId(UUID userId) {
        chatRepository.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteChat(UUID chatId,UUID userId) {
        Chat chat = findById(chatId);

        if (chat.getUserId() != null && !chat.getUserId().equals(userId)) {
            log.warn("User {} tried to delete chat {} belonging to {}", userId, chatId, chat.getUserId());

            throw new EntityNotFoundException("Chat not found");
        }

        chatRepository.delete(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID chatId) {
        return chatRepository.existsById(chatId);
    }

    @Override
    @Transactional(readOnly = true)
    public Chat findById(UUID chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> {
            log.warn("Chat with id {} not found.", chatId);
            return new EntityNotFoundException("Chat not found");
        });
    }

}
