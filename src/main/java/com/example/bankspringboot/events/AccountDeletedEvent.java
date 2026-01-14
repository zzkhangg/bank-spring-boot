package com.example.bankspringboot.events;

import java.util.UUID;

public record AccountDeletedEvent(UUID accountId, UUID userId) {
}
