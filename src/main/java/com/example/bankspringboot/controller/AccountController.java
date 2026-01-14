package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.AccountStatusHistoryResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
  public AccountResponse createAccount(
      @Valid @RequestBody CreateAccountRequest req) {
    return accountService.createAccount(req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/status")
  public AccountResponse updateAccountStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateAccountRequest req) {
    return accountService.updateAccountStatus(
        id, req);
  }

  @GetMapping("/{id}/status/history")
  public List<AccountStatusHistoryResponse> getAccountStatusHistory(@PathVariable UUID id) {
    return accountService.getAccountStatusHistory(id);
  }

  @GetMapping("/myAccounts")
  public List<AccountResponse> getMyAccounts() {
    return accountService.getAccountsForCurrentUser();
  }
}
