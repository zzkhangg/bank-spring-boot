package com.example.bankspringboot.scheduler;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import com.example.bankspringboot.service.ScheduledExecutionClaimer;
import com.example.bankspringboot.service.ScheduledExecutionFailureHandler;
import com.example.bankspringboot.service.ScheduledTransactionExecutor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Profile("!test")
public class ScheduledTransactionJob {

  ScheduledTransactionExecutor executor;
  ScheduledTransactionRepository scheduledTransactionRepository;
  ScheduledExecutionClaimer claimer;
  ScheduledExecutionFailureHandler failureHandler;

  @Scheduled(fixedDelay = 30000)
  public void executeScheduledTransactions() {
    log.info("Run scheduler");
    List<ScheduledTransaction> dueSchedules =
        scheduledTransactionRepository.findAllByNextRunAtBeforeAndStatus(
            Instant.now(), ScheduledStatus.ACTIVE);

    for (ScheduledTransaction schedule: dueSchedules) {
      UUID executionId;

      try {
          executionId = claimer.claim(
              schedule.getId(),
              schedule.getNextRunAt()
          );
      } catch (DataIntegrityViolationException e) {
          // Another instance already claimed it
          continue;
      }

      try {
          executor.execute(executionId);
      } catch (Exception e) {
          failureHandler.markFailed(executionId);
          log.error("Scheduled execution failed: {}", executionId, e);
      }
    }
  }
}
