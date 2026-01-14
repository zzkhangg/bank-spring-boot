package com.example.bankspringboot.events;

import com.example.bankspringboot.dto.account.AccountResponse;
import java.util.UUID;

public record AccountStatusUpdatedEvent(UUID accountId, UUID userId) {

}
