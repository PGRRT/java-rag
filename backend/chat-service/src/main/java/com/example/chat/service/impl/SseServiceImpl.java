package com.example.chat.service.impl;

import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.service.ChatBindingService;
import com.example.chat.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseServiceImpl implements SseService {
    // Map: chatId -> set of emitters (one emitter per connected client)
    private final Map<UUID, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private static final long DEFAULT_TIMEOUT = 60 * 60 * 1000L; // 1 hour

    private final ChatBindingService chatBindingService;

    @Override
    public boolean hasEmitters(UUID chatId) {
        Set<SseEmitter> set = emitters.get(chatId);
        return set != null && !set.isEmpty();
    }

    @Override
    public SseEmitter createEmitter(UUID chatId) {
        // create new emitter for this client
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // ensure a set exists for this chatId
        CopyOnWriteArraySet<SseEmitter> set = emitters.computeIfAbsent(chatId, id -> new CopyOnWriteArraySet<>());

        // if this is the first emitter for this chat -> bind chat in RabbitMQ
        if (set.isEmpty()) {
            try {
                chatBindingService.bindChat(chatId);
                log.info("Bound chat {} to exchange (first local subscriber)", chatId);
            } catch (Exception e) {
                log.error("Failed to bind chat {} on createEmitter", chatId, e);
                // decide: rethrow or continue; here we continue but log
            }
        }

        // add emitter to the set
        set.add(emitter);
        log.info("Added emitter for chat {} (total clients = {})", chatId, set.size());

        // lifecycle callbacks
        emitter.onCompletion(() -> {
            log.info("Emitter completed for chat {}", chatId);
            removeEmitter(chatId, emitter);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for chat {}", chatId);
            emitter.complete(); // close cleanly
            removeEmitter(chatId, emitter);
        });

        emitter.onError((Throwable ex) -> {
            log.warn("Emitter error for chat {}: {}", chatId, ex.getMessage());
            removeEmitter(chatId, emitter);
        });

        return emitter;
    }

    private void removeEmitter(UUID chatId, SseEmitter emitter) {
        CopyOnWriteArraySet<SseEmitter> set = emitters.get(chatId);
        if (set != null) {
            set.remove(emitter);
            log.info("Removed emitter for chat {} (remaining = {})", chatId, set.size());
            if (set.isEmpty()) {
                // remove the empty set to free memory
                emitters.remove(chatId);
                // last client disconnected -> unbind chat
                try {
                    chatBindingService.unBindChat(chatId);
                    log.info("Unbound chat {} from exchange (no local subscribers)", chatId);
                } catch (Exception e) {
                    log.error("Failed to unbind chat {} after last emitter removal", chatId, e);
                }
            }
        }
    }

    @Override
    public void emit(UUID chatId, ChatEvent eventName, String message) {
        CopyOnWriteArraySet<SseEmitter> set = emitters.get(chatId);
        if (set == null || set.isEmpty()) {
            log.debug("No emitters for chat {}, skipping emit", chatId);
            return;
        }

        String id = UUID.randomUUID().toString();
        for (SseEmitter emitter : set) {
            try {
                emitter.send(SseEmitter.event()
                        .id(id)
                        .name(eventName.name())
                        .data(message));
            } catch (IOException e) {
                log.warn("Failed to send to emitter for chat {} — removing emitter", chatId, e);
                // best-effort removal (this will also attempt unbind if it was last)
                removeEmitter(chatId, emitter);
            } catch (IllegalStateException e) {
                // emitter already completed/closed
                removeEmitter(chatId, emitter);
            }
        }
    }
}
