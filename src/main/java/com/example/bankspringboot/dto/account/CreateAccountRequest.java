package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateAccountRequest {
    @NotNull
    private Long customerId;

    @NotNull
    private AccountType accountType;

    private BigDecimal initialDeposit;

    public Long getCustomerId() {
        return customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }
}
