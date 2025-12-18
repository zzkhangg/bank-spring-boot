package com.example.bankspringboot.controller;


import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.service.AccountService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

  final private AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/{id}")
  public AccountResponse getAccount(@PathVariable UUID id) {
    return accountService.getAccount(id);
  }

  @PostMapping
  public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest req) {
    return accountService.createAccount(req);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public AccountResponse updateAccountStatus(@PathVariable UUID id,
      @Valid @RequestBody UpdateAccountRequest req) {
    return accountService.updateAccountStatus(id, req);
  }

  @GetMapping("/myAccounts")
  public List<AccountResponse> getMyAccounts() {
    return accountService.getMyAccounts();
  }
}
