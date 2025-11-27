package com.example.bankspringboot.domain;


import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    private Double balance;
    private Long transactionLimit;
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    private AccountType accountType;

    public Long getId() {
        return id;
    }

    public Long getTransactionLimit() {
        return transactionLimit;
    }

    public Customer getCustomer() {
        return customer;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Double getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransactionLimit(Long transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }
}
