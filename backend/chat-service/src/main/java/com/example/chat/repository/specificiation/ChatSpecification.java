package com.example.chat.repository.specificiation;

import com.example.chat.domain.entities.Chat;
import com.example.chat.domain.enums.ChatType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatSpecification {
    public static Specification<Chat> userChatsWithType(
            UUID userId, ChatType chatType
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (chatType == null || chatType == ChatType.GLOBAL) {
                Predicate globalChatPredicate = criteriaBuilder.equal(root.get("chatType"), ChatType.GLOBAL);
                predicates.add(globalChatPredicate);
            }

            if (chatType == null || chatType == ChatType.PRIVATE) {
                Predicate privateChatPredicate = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("chatType"), ChatType.PRIVATE),
                        criteriaBuilder.equal(root.get("userId"), userId)
                );
                predicates.add(privateChatPredicate);
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

    }
}
