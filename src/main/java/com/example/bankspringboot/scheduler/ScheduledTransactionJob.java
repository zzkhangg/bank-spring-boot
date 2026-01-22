package com.example.bankspringboot.scheduler;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import com.example.bankspringboot.service.ScheduledTransactionExecutor;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Profile("!test")
public class ScheduledTransactionJob {

  ScheduledTransactionExecutor executor;
  ScheduledTransactionRepository scheduledTransactionRepository;
  ScheduledTransactionExecutionRepository scheduledTransactionExecutionRepository;

  @Scheduled(fixedDelay = 30000)
  public void executeScheduledTransactions() {
    log.info("Run scheduler");
    List<ScheduledTransaction> scheduledTransactions =
        scheduledTransactionRepository.findAllByNextRunAtBeforeAndStatus(
            Instant.now(), ScheduledStatus.ACTIVE);
    scheduledTransactions.forEach(executor::execute);
  }
}
