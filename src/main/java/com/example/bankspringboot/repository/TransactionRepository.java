package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.financialreport.FinancialReportRow;
import com.example.bankspringboot.dto.statistics.TransactionReportView;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

  Page<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);

  boolean existsByIdAndCustomer_Email(UUID transactionId, String email);

  @Query(
      value =
          """
            SELECT
                YEAR(created_at) as year,
                WEEK(created_at) as period,
                AVG(amount) as avg_amount,
                MAX(amount) as max_amount,
                MIN(amount) as min_amount
            FROM transactions
            GROUP BY YEAR(created_at), WEEK(created_at)
          """,
      nativeQuery = true)
  List<TransactionReportView> weeklyTransactionReport();

  @Query(
      value =
          """
            SELECT
                YEAR(created_at) as year,
                QUARTER(created_at) as period,
                AVG(amount) as avg_amount,
                MAX(amount) as max_amount,
                MIN(amount) as min_amount
            FROM transactions
            GROUP BY YEAR(created_at), QUARTER(created_at)
          """,
      nativeQuery = true)
  List<TransactionReportView> quarterlyTransactionReport();

  @Query(
      value =
          """
            SELECT
                YEAR(created_at) as year,
                AVG(amount) as avg_amount,
                MAX(amount) as max_amount,
                MIN(amount) as min_amount
            FROM transactions
            GROUP BY YEAR(created_at)
          """,
      nativeQuery = true)
  List<TransactionReportView> yearlyTransactionReport();

  @Query(
      value =
          """
    SELECT
        DATE(created_at) as period,
        SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE 0 END) as total_deposit,
        SUM(CASE WHEN type = 'WITHDRAW' THEN amount ELSE 0 END) as total_withdrawal,
        SUM(CASE WHEN type = 'TRANSFER_OUT' THEN amount ELSE 0 END) as total_transfer,
        SUM(fee) as transaction_fee,
        SUM(
            CASE
                WHEN type = 'DEPOSIT' THEN amount
                WHEN type = 'TRANSFER_IN' THEN amount
                WHEN type = 'WITHDRAW' THEN -amount
                WHEN type = 'TRANSFER_OUT' THEN -amount
                ELSE 0
            END
        ) - SUM(fee) AS net_change
    FROM transactions
    WHERE created_at >= :from
      AND created_at < :toExclusive
    GROUP BY DATE(created_at)
    ORDER BY period ASC
  """,
      nativeQuery = true)
  List<FinancialReportRow> financialReportByDay(
      @Param("from") Instant from, @Param("toExclusive") Instant toExclusive);

  @Query(
      value =
          """
            SELECT
                DATE(created_at - INTERVAL DAY(created_at)-1 DAY) AS period,
                SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE 0 END ) as totalDeposit,
                SUM(CASE WHEN type = 'WITHDRAW' THEN amount ELSE 0 END ) as totalWithdrawal,
                SUM(CASE WHEN type = 'TRANSFER_OUT' THEN amount ELSE 0 END ) as totalTransfer,
                SUM(fee) as transactionFee,
                SUM(
                    CASE
                        WHEN type = 'DEPOSIT' THEN amount
                        WHEN type = 'TRANSFER_IN' THEN amount
                        WHEN type = 'WITHDRAW' THEN -amount
                        WHEN type = 'TRANSFER_OUT' THEN -amount
                        ELSE 0
                    END
                ) - SUM(fee) AS netChange
            FROM transactions
            WHERE created_at >= :from
                AND created_at < DATE_ADD(:to, INTERVAL 1 DAY)
            GROUP BY DATE(created_at - INTERVAL DAY(created_at)-1 DAY)
            ORDER BY period asc
          """,
      nativeQuery = true)
  List<FinancialReportRow> financialReportByMonth(Instant from, Instant to);

  @Query(
      value =
          """
            SELECT
                MAKEDATE(YEAR(created_at), 1) AS period,
                SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE 0 END ) as total_deposit,
                SUM(CASE WHEN type = 'WITHDRAW' THEN amount ELSE 0 END ) as total_withdrawal,
                SUM(CASE WHEN type = 'TRANSFER_OUT' THEN amount ELSE 0 END ) as total_transfer,
                SUM(fee) as transaction_fee,
                SUM(
                    CASE
                        WHEN type = 'DEPOSIT' THEN amount
                        WHEN type = 'TRANSFER_IN' THEN amount
                        WHEN type = 'WITHDRAW' THEN -amount
                        WHEN type = 'TRANSFER_OUT' THEN -amount
                        ELSE 0
                    END
                ) - SUM(fee) AS net_change
            FROM transactions
            WHERE created_at >= :from
                AND created_at < DATE_ADD(:to, INTERVAL 1 DAY)
            GROUP BY YEAR(created_at)
            ORDER BY period asc
          """,
      nativeQuery = true)
  List<FinancialReportRow> financialReportByYear(Instant from, Instant to);

  @Query(
      value =
          """
          SELECT
              SUM(
                  CASE
                      WHEN type = 'DEPOSIT' THEN amount
                      WHEN type = 'TRANSFER_IN' THEN amount
                      WHEN type = 'WITHDRAW' THEN -amount
                      WHEN type = 'TRANSFER_OUT' THEN -amount
                      ELSE 0
                  END
              ) - SUM(fee)
          FROM transactions
          WHERE created_at < :date;
          """,
      nativeQuery = true)
  BigDecimal getNetBalance(Instant date);

  int countByAccount_IdAndCreatedAtAfter(UUID accountId, Instant date);
}
