package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.transaction.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

  @Query(
      """
      SELECT count(t)
      from Transaction t
      WHERE t.account.balance >= :min AND (:max IS NULL OR t.account.balance < :max)
      """)
  Long countTransactionsInRange(BigDecimal min, BigDecimal max);

  Page<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

  boolean existsByIdAndCustomer_Email(UUID transactionId, String email);
}
