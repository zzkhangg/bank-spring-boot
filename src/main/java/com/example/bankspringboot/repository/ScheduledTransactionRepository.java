package com.example.bankspringboot.repository;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, UUID> {

  List<ScheduledTransaction> findAllByNextRunAtBeforeAndStatus(Instant before,
      ScheduledStatus status);
}
