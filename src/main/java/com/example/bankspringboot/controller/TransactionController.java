package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.service.TransactionService;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import java.util.List;
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
      @RequestBody DepositRequest req) {
    if (!accountId.equals(req.getAccountId())) {
      throw new IdInvalidException("Bad account id!");
    }
    return transactionService.deposit(req);
  }

  @PostMapping("/withdraw")
  public TransactionResponse withdraw(@PathVariable Long accountId,
      @RequestBody WithdrawalRequest req) {
    if (!accountId.equals(req.getAccountId())) {
      throw new IdInvalidException("Bad account id!");
    }
    return transactionService.withdraw(req);
  }

  @PostMapping("/transfer")
  public TransactionResponse transfer(@PathVariable Long accountId,
      @RequestBody TransferRequest req) {
    if (!accountId.equals(req.getSourceAccountId())) {
      throw new IdInvalidException("Bad account id!");
    }
    return transactionService.transfer(req);
  }

  @GetMapping("/transactions")
  public List<TransactionResponse> getTransactions(@PathVariable Long accountId) {
    return transactionService.getTransactions(accountId);
  }

  @GetMapping("/transactions/{transactionId}")
  public TransactionResponse getTransaction(@PathVariable Long transactionId) {
    return transactionService.getTransaction(transactionId);
  }
}
