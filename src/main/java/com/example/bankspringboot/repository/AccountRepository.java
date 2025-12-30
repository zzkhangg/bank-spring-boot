package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.customer.Customer;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.id = :id")
  Optional<Account> findByIdWithLock(@Param("id") UUID id);

  @Query("""
      SELECT count(a)
      FROM Account a
      WHERE a.balance >= :min AND (:max IS NULL OR a.balance < :max)
      """)
  Long countAccountsInRange(BigDecimal min, BigDecimal max);
  boolean existsByAccountNumber(@Param("accountNumber") String accountNumber);

  boolean existsByIdAndCustomer_Email(UUID id, String email);
  List<Account> findAllByCustomer_Email(String email);
}