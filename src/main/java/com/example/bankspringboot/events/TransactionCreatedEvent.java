package com.example.bankspringboot.events;

import java.util.UUID;

public record TransactionCreatedEvent(UUID accountId) {

}
