package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateAccountRequest {

    private AccountStatus status; // ACTIVE, FROZEN, CLOSED

    public AccountStatus getStatus() {
        return status;
    }
}
