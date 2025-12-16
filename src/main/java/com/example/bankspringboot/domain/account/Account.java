package com.example.bankspringboot.domain.account;


import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@SQLDelete(sql = "UPDATE account SET deleted = true WHERE id=?")
@FilterDef(name = "deletedAccountFilter", parameters = @ParamDef(name = "isDeleted", type = boolean.class))
@Filter(name = "deletedAccountFilter", condition = "deleted = :isDeleted")
@Table(
    name = "accounts",
    uniqueConstraints = @UniqueConstraint(columnNames = "account_number")
)
public class Account {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @Column(name = "account_number", nullable = false, unique = true)
  @Size(min = 10, max = 10)
  private String accountNumber;

  private BigDecimal balance;
  private BigDecimal transactionLimit;
  private boolean deleted = Boolean.FALSE;

  @CreatedDate
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private List<Transaction> transactions;

  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  @Enumerated(EnumType.STRING)
  private AccountType accountType;
}
