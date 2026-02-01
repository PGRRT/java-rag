package com.example.common.rabbitmq.events;

import java.util.UUID;

public record UserDeletedEvent(
        UUID userId
) {
}
