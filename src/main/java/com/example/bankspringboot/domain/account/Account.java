package com.example.bankspringboot.domain.account;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@Builder
@Table(
  name = "accounts",
  uniqueConstraints = @UniqueConstraint(columnNames = "account_number")
)
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "account_number", nullable = false, unique = true)
  @Size(min = 10, max = 10)
  private String accountNumber;

  private BigDecimal balance;
  private BigDecimal transactionLimit;

  @CreatedDate private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Transaction> transactions;

  @Enumerated(EnumType.STRING)
  private AccountStatus status;

  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
  List<AccountStatusHistory> accountStatusHistory;

  public void withdraw(BigDecimal amount) {
    if (amount.signum() <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    if (balance.compareTo(amount) < 0) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Insufficient funds");
    }

    balance = balance.subtract(amount);
  }

  public void deposit(BigDecimal amount) {
    balance = balance.add(amount);
  }
}
