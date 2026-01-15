package com.example.bankspringboot.domain.customer;

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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
@FilterDef(
    name = "deletedCustomerFilter",
    parameters = @ParamDef(name = "isDeleted", type = boolean.class))
@Filter(name = "deletedCustomerFilter", condition = "deleted = :isDeleted")
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

  @ManyToOne
  @JoinColumn(name = "customer_type_id", nullable = false)
  CustomerType customerType;

  @Embedded
  @NotNull
  Address address;

  @OneToMany(mappedBy = "customer")
  List<Account> accounts;

  @OneToMany(mappedBy = "customer")
  List<Transaction> transactions;
}
