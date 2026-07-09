package com.example.bankspringboot.domain.account;

import jakarta.persistence.*;

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
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "account_status_history")
public class AccountStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  Account account;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  AccountStatus status;

  @Column(name = "modified_at", nullable = false, updatable = false)
  Instant changedAt;

  @Column(nullable = false)
  String changedBy;

  @Column(nullable = false)
  String reason;

  @PrePersist
  void onCreate() {
    this.changedAt = Instant.now();
  }
}
