package com.example.bankspringboot.domain.customer;

import java.util.List;
import java.util.Objects;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;


@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id=?")
@FilterDef(name = "deletedCustomerFilter", parameters = @ParamDef(name = "isDeleted", type = boolean.class))
@Filter(name = "deletedCustomerFilter", condition = "deleted = :isDeleted")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private LocalDate birthdate;
  private String password;
  private boolean deleted = Boolean.FALSE;

  @Embedded
  private Address address;

  @OneToMany(mappedBy = "customer")
  private List<Account> accounts;

  @OneToMany(mappedBy = "customer")
  private List<Transaction> transactions;

  public Customer() {
  }

  public Customer(String firstName, String lastName, String email, String phone,
      LocalDate birthdate, String password, Address address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.birthdate = birthdate;
    this.password = this.password;
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof Customer)) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(this.id, customer.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    return "Customer{" + "id=" + this.id + ", name='" + this.lastName + this.firstName + '\'' + '}';
  }
}