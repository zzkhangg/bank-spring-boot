package com.example.bankspringboot.repository;

import com.example.bankspringboot.common.AccountBalanceType;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.id = :id")
  Optional<Account> findByIdWithLock(@Param("id") UUID id);

  @Query(
      """
      SELECT new com.example.bankspringboot.dto.statistics.AccountStatisticsDto(
            :balanceType,
            COUNT(DISTINCT a),
            COUNT(t)
            )
      FROM Account a
      JOIN a.transactions t
      WHERE a.balance >= :min AND (:max IS NULL OR a.balance < :max)
      """)
  AccountStatisticsDto findAccountTransactionNumByBalanceType(
      AccountBalanceType balanceType, BigDecimal min, BigDecimal max);

  boolean existsByAccountNumber(@Param("accountNumber") String accountNumber);

  boolean existsByIdAndCustomer_Email(UUID id, String email);

  List<Account> findByCustomer_Id(UUID customerId);
}
