package com.example.bankspringboot.domain.transaction;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.account.Account;
import jakarta.persistence.*;

import java.time.LocalDateTime;



@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private Long fee;
    private LocalDateTime time;

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
}
