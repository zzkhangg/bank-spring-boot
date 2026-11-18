package com.example.bankspringboot.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor 
public class ScheduledExecutionClaimer {

    ScheduledTransactionRepository scheduledTransactionRepository;
    ScheduledTransactionExecutionRepository scheduledTransactionExecutionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID claim(UUID scheduledTransactionId, Instant intendedRunAt) {
        ScheduledTransaction schedule =
            scheduledTransactionRepository.getReferenceById(scheduledTransactionId);

        ScheduledTransactionExecution execution = ScheduledTransactionExecution.builder()
            .scheduledTransaction(schedule)
            .intendedRunAt(intendedRunAt)
            .status(IdempotencyStatus.PROCESSING)
            .build();

        scheduledTransactionExecutionRepository.saveAndFlush(execution);

        return execution.getId();
    }
}
