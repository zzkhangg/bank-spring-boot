package com.example.bankspringboot.listeners;

import com.example.bankspringboot.cache.TransactionHistoryVersion;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.events.TransactionCreatedEvent;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.AlertService;
import com.example.bankspringboot.service.FraudDetectionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TransactionCreatedEventListener {

  TransactionHistoryVersion transactionHistoryVersion;
  TransactionRepository transactionRepository;
  FraudDetectionService fraudDetectionService;
  AlertService alertService;

  @TransactionalEventListener
  public void onTransactionCreated(TransactionCreatedEvent event) {
    Transaction transaction = transactionRepository.findById(event.transactionId()).orElseThrow();
    if (fraudDetectionService.isTooFast(transaction)) {
      alertService.createTooFastAlert(transaction);
    }

    transactionHistoryVersion.bump(transaction.getAccount().getId());
  }
}
