package com.example.bankspringboot.domain.customer;

import java.util.List;
import java.util.Objects;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.Transaction;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
@Table(
    name = "customers",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class Customer {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private LocalDate birthdate;

  @Column(nullable = false)
  private String password;

  private boolean deleted = Boolean.FALSE;

  @Embedded
  @Column(nullable = false)
  private Address address;

  @OneToMany(mappedBy = "customer")
  private List<Account> accounts;

  @OneToMany(mappedBy = "customer")
  private List<Transaction> transactions;

}