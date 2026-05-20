package com.example.bankspringboot.dto.transaction;

import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.domain.transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledTransactionRequest {
  /** ID of the account the money is sent to. */
  @NotBlank(message = "Destination account ID is required.")
  String toAccountId;

  /** Transaction amount (e.g. 100.50). */
  @NotNull(message = "Amount is required.")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero.")
  BigDecimal amount;

  /** Currency code, e.g. "AUD", "USD". */
  @NotBlank(message = "Currency is required.")
  String currency;

  /** Transaction type, e.g. "DEPOSIT, WITHDRAWAL, TRANSFER_OUT" */
  @NotNull(message = "Transaction type is required.")
  TransactionType transactionType;

  /** When this transaction should be executed. */
  @NotNull(message = "Start date and time is required.")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  LocalDateTime startAtLocal;

  /** Optional description shown on statements. */
  String description;

  /**
   * How often this should repeat (if at all). For a first version, you can skip this or use an
   * enum: ONE_TIME, DAILY, WEEKLY, MONTHLY
   */
  @NotNull(message = "Scheduled type is required.")
  ScheduledType scheduledType;

  @NotNull(message = "Time zone is required.")
  ZoneId zoneId;

  Integer intervalDays;

  DayOfWeek dayOfWeek;

  Integer dayOfMonth;
}
