package com.example.bankspringboot.security;

import com.example.bankspringboot.repository.TransactionRepository;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionSecurity {
  TransactionRepository transactionRepository;

  public boolean isCustomerOf(UUID transactionId, String email) {
    return transactionRepository.existsByIdAndCustomer_Email(transactionId, email);
  }
}
