package com.example.chat.repository;

import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.ChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID>, JpaSpecificationExecutor<Chat> {
    @EntityGraph(attributePaths = "messages")
    Optional<Chat> findChatWithMessagesById(UUID id);

//    @Query("""
//        select c from Chat c
//        where
//        (
//            (c.chatType = com.example.chat.domain.enums.ChatType.GLOBAL)
//            and
//            (:chatType is null or :chatType = com.example.chat.domain.enums.ChatType.GLOBAL)
//        )
//        or
//        (
//            (:userId is not null and c.chatType = com.example.chat.domain.enums.ChatType.PRIVATE and c.userId = :userId)
//            and
//            (:chatType is null or :chatType = com.example.chat.domain.enums.ChatType.PRIVATE)
//        )
//""")
//    Page<Chat> findChatsForUser(@Param("userId") UUID userId,@Param("chatType") ChatType chatType, Pageable pageable);

}
