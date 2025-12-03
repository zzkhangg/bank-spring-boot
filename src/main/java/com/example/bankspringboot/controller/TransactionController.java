package com.example.bankspringboot.controller;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.service.TransactionService;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts/{accountId}/")
public class TransactionController {
    final private TransactionService transactionService;


    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public TransactionResponse deposit(@PathVariable Long accountId,
                                       @RequestBody DepositRequest depositRequest) {
        return transactionService.deposit(depositRequest);
    }
}
