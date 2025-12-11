package com.example.bankspringboot.controller;

import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.service.TransactionService;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/{accountId}")
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
  public List<TransactionResponse> getTransactions(@PathVariable Long accountId,
      @RequestParam(name = "page", defaultValue = "1") int pageNumber,
      @RequestParam(name = "min-price", required = false) BigDecimal minPrice,
      @RequestParam(name = "max-price", required = false) BigDecimal maxPrice,
      @RequestParam(name = "transaction-type", required = false) TransactionType transactionType,
      @RequestParam(name = "created-before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate createdBefore,
      @RequestParam(name = "created-after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate createdAfter,
      @RequestParam(name = "created-after", required = false) String sort
  ) {

    return transactionService.getTransactions(accountId, pageNumber, minPrice, maxPrice,
        transactionType, createdBefore, createdAfter, sort);
  }

  @GetMapping("/transactions/{transactionId}")
  public TransactionResponse getTransaction(@PathVariable Long transactionId) {
    return transactionService.getTransaction(transactionId);
  }
}
