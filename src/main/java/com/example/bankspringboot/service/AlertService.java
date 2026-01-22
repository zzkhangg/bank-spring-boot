package com.example.bankspringboot.service;

import com.example.bankspringboot.common.AlertStatus;
import com.example.bankspringboot.domain.alert.Alert;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.repository.AlertRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlertService {
  AlertRepository alertRepository;

  public Alert createTooFastAlert(Transaction transaction) {
    Alert alert = new Alert();
    alert.setTransactionId(transaction.getId());
    alert.setReason("TOO_FAST");
    alert.setCreatedAt(Instant.now());
    alert.setStatus(AlertStatus.NEW);
    return alertRepository.save(alert);
  }
}
