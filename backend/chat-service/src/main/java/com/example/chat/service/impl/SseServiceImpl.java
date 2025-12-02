package com.example.chat.service.impl;

import com.example.chat.domain.enums.ChatEvent;
import com.example.chat.service.ChatBindingService;
import com.example.chat.service.SseService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseServiceImpl implements SseService {
    // Map: chatId -> set of emitters (one emitter per connected client)
    private final Map<UUID, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // Map: emitter -> scheduled ping task, so we can cancel ping when emitter is removed
    private final ConcurrentMap<SseEmitter, Future<?>> pingTasks = new ConcurrentHashMap<>();

    // Executor for scheduled pings. Size can be tuned depending on load
    // Here we use 2 threads as a reasonable default - measure and adjust as needed
//    private final ScheduledExecutorService scheduler =
//            Executors.newScheduledThreadPool(2);
//    Executor for ping tasks: virtual thread per task executor (requires VT enabled in JVM)

//    @Qualifier("pingExecutor")
    // Change that to use global virtual thread executor when available !
    private final ExecutorService pingExecutor = Executors.newVirtualThreadPerTaskExecutor();

    private static final long DEFAULT_TIMEOUT = 0L; // no timeout, emitter can be detected closed via pings
    private static final long PING_INTERVAL_MS = 5 * 60 * 1000L; // each 5 minutes send a ping to check connection liveness
    private static final long PING_DELAY_MS = 15 * 1000L; // 15s, initial delay before first ping
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

        // add emitter to the set
        set.add(emitter);
        // if this is the first emitter for this chat -> bind chat in RabbitMQ
        if (set.size() == 1) {
            try {
                chatBindingService.bindChat(chatId);
                log.info("Bound chat {} to exchange (first local subscriber)", chatId);
            } catch (Exception e) {
                log.error("Failed to bind chat {} on createEmitter", chatId, e);
            }
        }

        log.info("Added emitter for chat {} (total clients = {})", chatId, set.size());

        // lifecycle callbacks
        emitter.onCompletion(() -> {
            log.info("Emitter completed for chat {}", chatId);
            cleanupEmitter(chatId, emitter,null);
        });

        emitter.onTimeout(() -> {
            log.info("Emitter timed out for chat {}", chatId);
            try {
                // cleanup will be done in onCompletion
                emitter.complete();
            } catch (Exception ignore) {}
        });

        emitter.onError((Throwable e) -> {
            cleanupEmitter(chatId, emitter,e);
            log.info("Emitter error for chat {}: {}", chatId, e.getClass().getSimpleName());
        });

        Future<?> future = startPing(chatId, emitter);

        if (future != null) {
            pingTasks.put(emitter, future);
        }

        return emitter;
    }

    private void handleEmitterError(UUID chatId, SseEmitter emitter, Exception e) {
        try {
            emitter.completeWithError(e);
        } catch (Exception ex) {
            // ioexception is expected if emitter is already closed
            log.debug("completeWithError failed for chat {}: {}", chatId, ex.toString());
        }
    }

    // start periodic pings to keep connection alive and detect closed connections
    // returns Future representing the ping task
    // we use a normal ExecutorService with virtual threads
    private Future<?> startPing(UUID chatId, SseEmitter emitter) {
        try {
            return pingExecutor.submit(() -> {
                try {
                    Thread.sleep(PING_DELAY_MS);
                }   catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

               while(!Thread.currentThread().isInterrupted()) {
                   try {
                       emitter.send(SseEmitter.event().name(ChatEvent.PING.name()).data("ping"));

                       log.debug("Sent PING to emitter for chat {}", chatId);
                   } catch (IOException e) {
                       log.debug("Failed to send to emitter for chat {} — removing emitter", chatId);
                       handleEmitterError(chatId, emitter, e);
                       break;
                   } catch (Exception e) {
                       log.debug("Unexpected exception sending to emitter for chat {}", chatId);
                       handleEmitterError(chatId, emitter, e);
                       break;
                   }
                   try {
                       Thread.sleep(PING_INTERVAL_MS);
                   } catch (InterruptedException ie) {
                       // interrupted during sleep -> time to stop
                       Thread.currentThread().interrupt();
                       break;
                   }
               }

            });

        } catch (RejectedExecutionException rex) {
            log.warn("Ping executor rejected task for chat {} — cleaning up emitter", chatId);
            cleanupEmitter(chatId, emitter, rex);
            return null;
        }
    }

    private void cleanupEmitter(UUID chatId, SseEmitter emitter, Throwable cause) {
        Future<?> f = pingTasks.remove(emitter);
        if (f != null) {
            try {
                f.cancel(true);
            } catch (Exception ex) {
                log.debug("Failed to cancel ping for chat {}: {}", chatId, ex.toString());
            }
        }

        CopyOnWriteArraySet<SseEmitter> set = emitters.get(chatId);
        if (set != null) {
            // remove emitter
            set.remove(emitter);

            log.info("Removed emitter for chat {} (remaining = {})", chatId, set.size());
            if (set.isEmpty()) {
                // remove the empty set to free memory, using remove(key, value) to ensure no new emitters were added meanwhile
                boolean removed =  emitters.remove(chatId, set);

                if (removed) {
                    try {
                        // last client disconnected -> unbind chat
                        chatBindingService.unBindChat(chatId);
                        log.debug("Unbound chat {} from exchange (no local subscribers)", chatId);
                    } catch (Exception e) {
                        log.debug("Failed to unbind chat {} after last emitter removal", chatId);
                    }
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

                log.debug("Failed to send to emitter for chat {} — removing emitter", chatId);
                handleEmitterError(chatId, emitter, e);
            } catch (Exception e) {
                log.debug("Unexpected exception sending to emitter for chat {}", chatId);
                handleEmitterError(chatId, emitter, e);
            }
        }
    }
}
