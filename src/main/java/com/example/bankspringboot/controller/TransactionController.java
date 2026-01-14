package com.example.bankspringboot.controller;

import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.service.IdempotencyService;
import com.example.bankspringboot.service.TransactionFacade;
import com.example.bankspringboot.util.HashUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/{accountId}")
@Slf4j
public class TransactionController {

  private final TransactionFacade transactionFacade;
  private final IdempotencyService idempotencyService;
  private final ObjectMapper objectMapper;

  public TransactionController(
      TransactionFacade transactionFacade,
      IdempotencyService idempotencyService,
      ObjectMapper objectMapper) {
    this.transactionFacade = transactionFacade;
    this.idempotencyService = idempotencyService;
    this.objectMapper = objectMapper;
  }

  @PostMapping("/deposit")
  public TransactionResponse deposit(
      @PathVariable UUID accountId, @RequestBody DepositRequest req) {
    return transactionFacade.deposit(accountId, req);
  }

  @PostMapping("/withdraw")
  public TransactionResponse withdraw(
      @PathVariable UUID accountId, @RequestBody WithdrawalRequest req) {
    return transactionFacade.withdraw(accountId, req);
  }

  @PostMapping("/transfer")
  public TransactionResponse transfer(
      @PathVariable UUID accountId,
      @RequestHeader("Idempotency-Key") String key,
      @RequestBody TransferRequest req)
      throws JsonProcessingException {
    String payloadJson = objectMapper.writeValueAsString(req);
    String requestHash = HashUtil.sha256(payloadJson);

    return idempotencyService.handleIdempotency(
        key,
        requestHash,
        TransactionResponse.class,
        () -> transactionFacade.transfer(accountId, req));
  }

  @GetMapping("/transactions")
  public List<TransactionResponse> getTransactions(
      @PathVariable UUID accountId,
      @RequestParam(name = "page", defaultValue = "1", required = false) int pageNumber,
      @RequestParam(name = "min-price", required = false) BigDecimal minPrice,
      @RequestParam(name = "max-price", required = false) BigDecimal maxPrice,
      @RequestParam(name = "transaction-type", required = false) TransactionType transactionType,
      @RequestParam(name = "created-before", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate createdBefore,
      @RequestParam(name = "created-after", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate createdAfter,
      @RequestParam(name = "created-after", required = false) String sort) {

    return transactionFacade.getTransactions(
        accountId,
        pageNumber,
        minPrice,
        maxPrice,
        transactionType,
        createdBefore,
        createdAfter,
        sort);
  }

  @GetMapping("/transactions/{transactionId}")
  public TransactionResponse getTransaction(
      @PathVariable UUID accountId, @PathVariable UUID transactionId) {
    return transactionFacade.getTransaction(accountId, transactionId);
  }
}
