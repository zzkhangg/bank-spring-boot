package com.example.bankspringboot.events;

import java.util.UUID;

public record AccountStatusUpdatedEvent(UUID accountId, UUID userId) {}
