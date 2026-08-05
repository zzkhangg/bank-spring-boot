package com.example.bankspringboot.domain.customer;

import com.example.bankspringboot.common.CustomerStatus;
import com.example.bankspringboot.domain.User;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customers")
public class Customer extends User {

  @NotNull
  @Column(nullable = false)
  String firstName;

  @NotNull
  @Column(nullable = false)
  String lastName;

  @NotNull
  @Column(nullable = false)
  String phoneNumber;

  @NotNull
  @Column(nullable = false)
  LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  CustomerStatus status;

  @ManyToOne
  @JoinColumn(name = "customer_type_id", nullable = false)
  CustomerType customerType;

  @Embedded @NotNull Address address;

  @OneToMany(mappedBy = "customer")
  List<Account> accounts;

  @OneToMany(mappedBy = "customer")
  List<Transaction> transactions;
}
