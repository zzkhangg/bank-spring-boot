package com.example.bankspringboot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {
  @Id
  private String id;

  @Column(name = "expiry_date")
  private Instant expiryDate;
}
