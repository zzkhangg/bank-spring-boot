package com.example.bankspringboot.dto.transaction;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for a client-initiated Transfer transaction. Represents funds moving from
 * one account to another.
 */
@Getter
@Setter
public class TransferRequest {
  /** The ID of the account to deposit the funds into. */
  @NotNull(message = "Destination account ID is required.")
  private UUID destinationAccountId;

  /** The amount of money to transfer. Must be positive and non-zero. */
  @NotNull(message = "Amount is required.")
  @DecimalMin(value = "0.01", inclusive = true, message = "Transfer amount must be at least 0.01.")
  @Digits(
      integer = 12,
      fraction = 2,
      message = "Amount can have at most 12 integer digits and 2 decimal places.")
  private BigDecimal amount;

  /** The currency code (e.g., "USD", "EUR"). */
  @NotBlank(message = "Currency is required.")
  @Size(min = 3, max = 3, message = "Currency must be a 3-letter code, such as USD.")
  private String currency;

  /** A description of the transfer. */
  @Size(max = 255, message = "Description cannot exceed 255 characters.")
  private String description;

  /** Address */
  @Valid private Address address;

  /** The channel through which the withdrawal is being performed (e.g., "ATM", "Online"). */
  @NotNull(message = "Transaction channel is required.")
  private TransactionChannel channel;
}
