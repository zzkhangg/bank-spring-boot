package com.example.bankspringboot.domain.scheduledtransaction;

import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
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
      @UniqueConstraint(columnNames = {"scheduled_tx_id", " execution_time", " attempt_no"})
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

  @Column(name = "scheduled_tx_id", nullable = false, updatable = false)
  UUID scheduledTransactionId;

  @Column(name = "intended_run_at", nullable = false)
  Instant intendedRunAt;

  @Column(name = "attempt_no", nullable = false)
  int attemptNum;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  IdempotencyStatus status;
}
