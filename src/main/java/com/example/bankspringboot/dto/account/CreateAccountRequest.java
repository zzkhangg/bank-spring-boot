package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateAccountRequest {

  @NotNull
  private Long customerId;

  @NotNull
  private AccountType accountType;

  @NotNull
  @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative.")
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
