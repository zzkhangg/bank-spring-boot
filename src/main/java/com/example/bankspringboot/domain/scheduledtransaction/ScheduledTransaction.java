package com.example.bankspringboot.domain.scheduledtransaction;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.common.Auditable;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.domain.transaction.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledTransaction extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(nullable = false)
  BigDecimal amount;

  String description;

  String currency;

  @Column(nullable = false, name = "transaction_type")
  @Enumerated(EnumType.STRING)
  TransactionType transactionType;

  @Embedded
  Address address;

  @Enumerated(EnumType.STRING)
  TransactionChannel channel;

  @ManyToOne
  @JoinColumn(nullable = false, name = "from_account_id")
  Account fromAccount;

  @ManyToOne
  @JoinColumn(nullable = false, name = "to_account_id")
  Account toAccount;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  ScheduledType scheduledType;

  @Column(name = "interval_days")
  int intervalDays;

  @Column(name = "day_of_week")
  @Enumerated(EnumType.STRING)
  DayOfWeek dayOfWeek;

  @Column(name = "day_of_month")
  int dayOfMonth;

  @Column(nullable = false, name = "start_at_local")
  LocalDateTime startAtLocal;

  @Column(nullable = false, name = "time_zone", updatable = false)
  ZoneId zoneId;

  @Column(name = "next_run_at")
  Instant nextRunAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  ScheduledStatus status;

  @Column(name = "last_intended_local")
  LocalDateTime lastIntendedLocal;
}
