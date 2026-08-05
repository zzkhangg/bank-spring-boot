package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(

  String accountId, // UUID or String
  String accountNumber, // public-facing account number
  AccountType accountType, // SAVINGS, CHECKING, etc.
  BigDecimal balance, // current balance
  BigDecimal transactionLimit,
  AccountStatus status, // ACTIVE, FROZEN, CLOSED
  Instant createdAt
  )
{}
