package com.example.bankspringboot.listeners;

import com.example.bankspringboot.cache.TransactionHistoryVersion;
import com.example.bankspringboot.events.TransactionCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionHistoryVersionCachePutListener {

  private final TransactionHistoryVersion transactionHistoryVersion;

  public TransactionHistoryVersionCachePutListener(
      TransactionHistoryVersion transactionHistoryVersion) {
    this.transactionHistoryVersion = transactionHistoryVersion;
  }

  @TransactionalEventListener
  public void onTransactionCreated(TransactionCreatedEvent event) {
    transactionHistoryVersion.bump(event.accountId());
  }
}
