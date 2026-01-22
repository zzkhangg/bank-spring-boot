package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CreateAccountRequest {

  @NotNull @Email String email;

  @NotNull AccountType accountType;

  @NotNull
  @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative.")
  BigDecimal initialDeposit;

  @DecimalMin(value = "0.00", message = "Transaction limit cannot be negative.")
  BigDecimal transactionLimit;
}
