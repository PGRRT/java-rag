package com.example.chat.repository;

import com.example.chat.domain.entities.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @EntityGraph(attributePaths = "messages")
    Optional<Chat> findChatWithMessagesById(UUID id);


    @Query("select c from Chat c where (:includeGlobal = true and c.chatType = com.example.chat.domain.enums.ChatType.GLOBAL)   " +
            "or (:userId is not null and  c.chatType = com.example.chat.domain.enums.ChatType.PRIVATE and c.userId = :userId)")
    Page<Chat> findChatsForUser(@Param("userId") UUID userId, boolean includeGlobal, Pageable pageable);

}
