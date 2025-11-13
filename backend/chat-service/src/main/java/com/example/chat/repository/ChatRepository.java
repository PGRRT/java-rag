package com.example.chat.repository;

import com.example.chat.domain.entities.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @EntityGraph(attributePaths = "messages")
    Optional<Chat> findChatWithMessagesById(UUID id);
}
