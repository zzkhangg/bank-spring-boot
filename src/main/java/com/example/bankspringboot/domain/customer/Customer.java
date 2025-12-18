package com.example.bankspringboot.domain.customer;

import com.example.bankspringboot.domain.Role;
import com.example.bankspringboot.domain.User;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.Transaction;
import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
@FilterDef(name = "deletedCustomerFilter", parameters = @ParamDef(name = "isDeleted", type = boolean.class))
@Filter(name = "deletedCustomerFilter", condition = "deleted = :isDeleted")
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("CUSTOMER")
@SuperBuilder
public class Customer extends User {
  @NotNull
  String firstName;

  @NotNull
  String lastName;

  @NotNull
  String phoneNumber;

  @NotNull
  LocalDate birthDate;

  boolean deleted;

  @Embedded
  @NotNull
  Address address;

  @OneToMany(mappedBy = "customer")
  List<Account> accounts;

  @OneToMany(mappedBy = "customer")
  List<Transaction> transactions;
}