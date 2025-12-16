package com.example.bankspringboot.dto.transaction;

import java.math.BigDecimal;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;

/**
 * Data Transfer Object for a client-initiated Deposit transaction. Represents funds moving INTO an
 * account.
 */
@Getter
public class DepositRequest {

  /**
   * The ID of the account that will receive the funds.
   */
  @NotBlank(message = "Account ID is mandatory.")
  private UUID accountId;

  /**
   * The amount of money to deposit. Must be positive and non-zero. Uses BigDecimal for precise
   * financial calculations.
   */
  @NotNull(message = "Amount is mandatory.")
  @DecimalMin(value = "0.01", inclusive = true, message = "The deposit amount must be positive.")
  @Digits(integer = 12, fraction = 2, message = "Amount can have at most 12 integer digits and 2 fraction digits.")
  private BigDecimal amount;

  /**
   * An optional note or reference from the client.
   */
  @Size(max = 255, message = "Reference note cannot exceed 255 characters.")
  private String description;

  /**
   * The currency code (e.g., "USD", "EUR").
   */
  @NotBlank(message = "Currency is mandatory.")
  @Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g., USD).")
  private String currency;

  /**
   * Address
   */
  @Valid
  private Address address;

  /**
   * The channel through which the deposit is being performed (e.g., "ATM", "Online").
   */
  @NotBlank(message = "Channel is mandatory.")
  @NotNull
  private TransactionChannel channel;
}
