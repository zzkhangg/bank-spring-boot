package com.example.bankspringboot.service.specifications;

import com.example.bankspringboot.domain.account.Account_;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.domain.transaction.Transaction_;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecs {

  public static Specification<Transaction> priceGreaterThanOrEqualTo(BigDecimal price) {
    return (root, criteriaQuery, criteriaBuilder)
        -> criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.AMOUNT), price);
  }

  public static Specification<Transaction> belongsToAccount(UUID accountId) {
    return ((root, query, criteriaBuilder)
        -> criteriaBuilder.equal(root.get(Transaction_.account).get(Account_.id), accountId));
  }

  public static Specification<Transaction> priceLessThanOrEqualTo(BigDecimal price) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.AMOUNT), price);
  }

  public static Specification<Transaction> hasTransactionType(TransactionType type) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Transaction_.type),
        type);
  }

  public static Specification<Transaction> createdDateAfter(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo( root.get(Transaction_.createdAt),
        startOfDay));
  }

  public static Specification<Transaction> createdDateBefore(LocalDate date) {
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
    return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo( root.get(Transaction_.createdAt),
        endOfDay));
  }
}
