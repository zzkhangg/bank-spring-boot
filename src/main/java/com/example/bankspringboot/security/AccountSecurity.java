package com.example.bankspringboot.security;

import com.example.bankspringboot.repository.AccountRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountSecurity {

  private final AccountRepository accountRepository;

  public boolean isOwner(UUID accountId, String email) {
    return accountRepository.existsByIdAndCustomer_Email(accountId, email);
  }
}
