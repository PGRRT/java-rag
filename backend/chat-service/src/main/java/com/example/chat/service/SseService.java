package com.example.chat.service;

import com.example.chat.domain.enums.ChatEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public interface SseService {
    SseEmitter createEmitter(UUID chatId);
    boolean hasEmitters(UUID chatId);
    void emit(UUID chatId, ChatEvent eventName, String message);
}
