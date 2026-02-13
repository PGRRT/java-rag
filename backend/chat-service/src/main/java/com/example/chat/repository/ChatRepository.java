package com.example.chat.repository;

import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.ChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID>, JpaSpecificationExecutor<Chat> {
//    @EntityGraph(attributePaths = "messages")
//    Optional<Chat> findChatWithMessagesById(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("delete from Chat c where c.userId = :userId")
    void deleteAllByUserId(UUID userId);

}
