package com.example.bankspringboot.dto.transaction;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {

    private Long transactionId;
    private Long accountId;
    private Long customerId;
    private BigDecimal amount;
    private TransactionStatus status; // SUCCESS, FAILED, PENDING
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal fee;
    private Address address;
    private TransactionType transactionType;

    public TransactionResponse(Long transactionId, Long accountId, Long customerId, BigDecimal amount,
                               TransactionStatus status, LocalDateTime createdAt, String description, BigDecimal fee,
                               Address address, TransactionType transactionType) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.description = description;
        this.fee = fee;
        this.address = address;
        this.transactionType = transactionType;
    }
}
