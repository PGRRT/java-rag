package com.example.chat.domain.entities;

import com.example.chat.domain.enums.ChatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "chats", indexes = {
        @Index(name = "idx_chats_user_id", columnList = "user_id"),
        @Index(name = "idx_chats_type_user", columnList = "chatType, user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Chat extends BaseClass<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Application will have also global chats that does not require user association
    @Column(name="user_id")
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Builder.Default
    @OneToMany(mappedBy = "chat", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    List<Message> messages = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @AssertTrue(message = "userId must be set for PRIVATE chats")
    private boolean isUserIdValid() {
        if (chatType == ChatType.PRIVATE && userId == null) {
            return false;
        }
        return true;
    }
}
