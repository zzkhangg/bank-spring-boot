package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.repository.IdempotencyKeyRepository;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyKeyWriter {

  IdempotencyKeyRepository idempotencyKeyRepository;
  ScheduledTransactionExecutionRepository scheduledTransactionExecutionRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void write(IdempotencyKey idempotencyKey) {
    idempotencyKeyRepository.save(idempotencyKey);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void write(ScheduledTransactionExecution execution) {
    scheduledTransactionExecutionRepository.save(execution);
  }
}
