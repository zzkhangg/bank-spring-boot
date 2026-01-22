package com.example.bankspringboot.service.specifications;

import com.example.bankspringboot.domain.account.Account_;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.domain.transaction.Transaction_;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecs {

  public static Specification<Transaction> priceGreaterThanOrEqualTo(BigDecimal price) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.AMOUNT), price);
  }

  public static Specification<Transaction> belongsToAccount(UUID accountId) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Transaction_.account).get(Account_.id), accountId));
  }

  public static Specification<Transaction> priceLessThanOrEqualTo(BigDecimal price) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.AMOUNT), price);
  }

  public static Specification<Transaction> hasTransactionType(TransactionType type) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Transaction_.type), type);
  }

  public static Specification<Transaction> createdDateAfter(LocalDate date) {
    Instant start = date.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.greaterThanOrEqualTo(root.get(Transaction_.createdAt), start));
  }

  public static Specification<Transaction> createdDateBefore(LocalDate date) {
    Instant end = date.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.lessThanOrEqualTo(root.get(Transaction_.createdAt), end));
  }
}
