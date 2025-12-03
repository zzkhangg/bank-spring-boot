package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long accountId;      // UUID or String
    private String accountNumber;  // public-facing account number
    private AccountType accountType;    // SAVINGS, CHECKING, etc.

    private BigDecimal balance;    // current balance
    private BigDecimal transactionLimit;


    private AccountStatus status;         // ACTIVE, FROZEN, CLOSED

    private LocalDateTime createdAt;

    public AccountResponse(Long accountId, String accountNumber, AccountType accountType, BigDecimal balance, BigDecimal transactionLimit, AccountStatus status, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.transactionLimit = transactionLimit;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
