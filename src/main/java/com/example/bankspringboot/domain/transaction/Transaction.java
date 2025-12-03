package com.example.bankspringboot.domain.transaction;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Currency is mandatory.")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g., USD).")
    private String currency;

    @Enumerated(EnumType.STRING)
    private TransactionChannel channel;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
