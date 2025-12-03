package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.service.TransactionService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@PathVariable Long accountId, @RequestBody WithdrawalRequest withdrawalRequest) {
        return transactionService.withdraw(withdrawalRequest);
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@PathVariable Long accountId, @RequestBody TransferRequest transferRequest) {
        return transactionService.transfer(transferRequest);
    }
}
