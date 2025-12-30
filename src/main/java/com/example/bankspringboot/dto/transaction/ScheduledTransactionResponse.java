package com.example.bankspringboot.dto.transaction;

import com.example.bankspringboot.common.ScheduledType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduledTransactionResponse {
  String id;

  String status;

  BigDecimal amount;
  String currency;
  String toAccountId;
  String description;

  ScheduledType scheduledType;
  Integer intervalDays;
  DayOfWeek dayOfWeek;
  Integer dayOfMonth;

  LocalDateTime startAtLocal;
  LocalDateTime nextRunAt;

  TimeZone timeZone;
}
