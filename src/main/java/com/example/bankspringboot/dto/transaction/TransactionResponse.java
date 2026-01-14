package com.example.bankspringboot.dto.transaction;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {

  private String transactionId;
  private String accountId;
  private String customerId;
  private BigDecimal amount;
  private TransactionStatus status; // SUCCESS, FAILED, PENDING
  private LocalDateTime createdAt;
  private String description;
  private BigDecimal fee;
  private Address address;
  private TransactionType transactionType;
  private String currency;
}
