package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountSummaryResponse(
        UUID accountId,
        String accountNumber,
        BigDecimal balance,
        AccountStatus status
) {
}
