package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTransactionWriter {

  private final ScheduledTransactionRepository scheduledTransactionRepository;

  public ScheduledTransactionWriter(ScheduledTransactionRepository scheduledTransactionRepository) {
    this.scheduledTransactionRepository = scheduledTransactionRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markNextRunAt(Instant nextRunAt, ScheduledTransaction tx) {
    tx.setNextRunAt(nextRunAt);
    scheduledTransactionRepository.save(tx);
  }
}
