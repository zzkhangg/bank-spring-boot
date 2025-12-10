package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
    JpaSpecificationExecutor<Transaction> {

  @Query("""
      SELECT count(t)
      from Transaction t
      WHERE t.account.balance >= :min AND (:max IS NULL OR t.account.balance < :max)
      """)
  Long countTransactionsInRange(BigDecimal min, BigDecimal max);
  List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);
  Page<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);
}