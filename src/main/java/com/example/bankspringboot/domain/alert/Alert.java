package com.example.bankspringboot.domain.alert;

import com.example.bankspringboot.common.AlertStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "alert")
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Alert {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long Id;

  UUID transactionId;

  @Column(nullable = false)
  String reason;

  @Enumerated(EnumType.STRING)
  AlertStatus status;

  Instant createdAt;
}
