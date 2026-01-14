package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledTransactionExecutionRepository
    extends JpaRepository<ScheduledTransactionExecution, UUID> {}
