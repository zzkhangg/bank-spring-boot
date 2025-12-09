package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {

  private Long accountId;      // UUID or String
  private String accountNumber;  // public-facing account number
  private AccountType accountType;    // SAVINGS, CHECKING, etc.

  private BigDecimal balance;    // current balance
  private BigDecimal transactionLimit;


  private AccountStatus status;         // ACTIVE, FROZEN, CLOSED

  private LocalDateTime createdAt;
}
