package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AccountResponse {

  String accountId; // UUID or String
  String accountNumber; // public-facing account number
  AccountType accountType; // SAVINGS, CHECKING, etc.
  BigDecimal balance; // current balance
  BigDecimal transactionLimit;
  AccountStatus status; // ACTIVE, FROZEN, CLOSED
  LocalDateTime createdAt;
}
