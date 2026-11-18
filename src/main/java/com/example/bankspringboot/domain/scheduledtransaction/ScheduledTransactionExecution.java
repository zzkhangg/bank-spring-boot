package com.example.bankspringboot.domain.scheduledtransaction;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name = "scheduled_tx_execution",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"scheduled_tx_id", "intended_run_at"})
    })
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class ScheduledTransactionExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "scheduled_tx_id",
    nullable = false,
    updatable = false
  )
  ScheduledTransaction scheduledTransaction;

  @Column(name = "intended_run_at", nullable = false)
  Instant intendedRunAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  IdempotencyStatus status;
}
