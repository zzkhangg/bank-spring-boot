package com.example.bankspringboot.service;

import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.mapper.AccountMapper;
import com.example.bankspringboot.repository.AccountRepository;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AccountQueryService {

  AccountRepository accountRepository;
  AccountMapper accountMapper;

  @Cacheable(value = "accountsByUser", key = "#userId")
  public List<AccountResponse> getAccounts(UUID userId) {
    return accountRepository.findByCustomer_Id(userId).stream()
        .map(accountMapper::toResponse)
        .toList();
  }
}
