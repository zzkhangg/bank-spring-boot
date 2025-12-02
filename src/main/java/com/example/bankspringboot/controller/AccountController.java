package com.example.bankspringboot.controller;


import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    final private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return null;
    }

    @PostMapping()
    public Account createAccount(@RequestBody CreateAccountRequest req) {
        // return accountService.createAccount(req);
        return null;
    }
}
