package com.example.bankspringboot.controller;

import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.dto.account.*;
import com.example.bankspringboot.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountController {

  AccountService accountService;

  @GetMapping("/{id}")
  public AccountResponse getAccount(@PathVariable UUID id) {
    return accountService.getAccount(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest req) {
    return accountService.createAccount(req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}/status")
  public AccountResponse updateAccountStatus(
          @PathVariable UUID id, @Valid @RequestBody UpdateAccountRequest req) {
    return accountService.updateAccountStatus(id, req);
  }

  @GetMapping("/{id}/status/history")
  public List<AccountStatusHistoryResponse> getAccountStatusHistory(@PathVariable UUID id) {
    return accountService.getAccountStatusHistory(id);
  }

  @GetMapping("/myAccounts")
  public List<AccountSummaryResponse> getMyAccounts() {
    return accountService.getAccountsForCurrentUser();
  }

  @GetMapping()
  public List<AccountResponse> getAccounts(@RequestParam(required = false, defaultValue = "ACTIVE") AccountStatus status) {
    return accountService.getAccounts(status);
  }
}