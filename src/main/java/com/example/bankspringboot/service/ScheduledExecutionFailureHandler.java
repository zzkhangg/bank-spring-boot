package com.example.bankspringboot.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor 
public class ScheduledExecutionFailureHandler {

    ScheduledTransactionExecutionRepository executionRepository;
 
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID executionId) {
        ScheduledTransactionExecution execution =
            executionRepository.findById(executionId).orElseThrow();

        execution.setStatus(IdempotencyStatus.FAILED);
    }
}
