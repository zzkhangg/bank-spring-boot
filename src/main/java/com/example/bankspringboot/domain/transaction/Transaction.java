package com.example.bankspringboot.domain.transaction;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.account.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime createdAt;

    private String description;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String currency;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
