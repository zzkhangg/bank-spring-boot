package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.financialreport.FinancialReportView;
import com.example.bankspringboot.dto.statistics.TransactionReportView;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

  @Query(
      value = """
        SELECT 
            YEAR(created_at) as year,
            WEEK(created_at) as period,
            AVG(amount) as avg_amount,
            MAX(amount) as max_amount,
            MIN(amount) as min_amount
        FROM transactions
        GROUP BY YEAR(created_at), WEEK(created_at)
      """, nativeQuery = true)
  List<TransactionReportView> weeklyReport();

  @Query(
      value = """
        SELECT 
            YEAR(created_at) as year,
            QUARTER(created_at) as period,
            AVG(amount) as avg_amount,
            MAX(amount) as max_amount,
            MIN(amount) as min_amount
        FROM transactions
        GROUP BY YEAR(created_at), QUARTER(created_at)
      """, nativeQuery = true)
  List<TransactionReportView> quarterlyReport();

  @Query(
      value = """
        SELECT 
            YEAR(created_at) as year,
            AVG(amount) as avg_amount,
            MAX(amount) as max_amount,
            MIN(amount) as min_amount
        FROM transactions
        GROUP BY YEAR(created_at)
      """, nativeQuery = true)
  List<TransactionReportView> yearlyReport();

  @Query(
      value = """
        SELECT 
            SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE 0 END ) as total_deposit,
            SUM(CASE WHEN type = 'WITHDRAW' THEN amount ELSE 0 END ) as total_withdrawal,
            SUM(CASE WHEN type = 'TRANSFER_OUT' THEN amount ELSE 0 END ) as total_transfer
        FROM transactions
        WHERE created_at >= :from AND created_at <= :to
      """,
      nativeQuery = true
  )
  FinancialReportView getFinancialReport(LocalDate from, LocalDate to);
}
