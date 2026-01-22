package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.repository.TransactionRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FraudDetectionService {
  TransactionRepository transactionRepository;

  static int FAST_TX_LIMIT = 5;
  static Duration FAST_WINDOW = Duration.ofSeconds(10);

  public boolean isTooFast(Transaction transaction) {
    Instant checkedAfter = transaction.getCreatedAt().minus(FAST_WINDOW);
    int count =
        transactionRepository.countByAccount_IdAndCreatedAtAfter(
            transaction.getAccount().getId(), checkedAfter);
    return count >= FAST_TX_LIMIT;
  }
}
