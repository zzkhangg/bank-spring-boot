package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.transaction.ScheduledTransactionRequest;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionResponse;
import com.example.bankspringboot.service.ScheduledTransactionService;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/accounts/{accountId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTransactionController {
  ScheduledTransactionService scheduleService;

  @PostMapping("/scheduled-transactions")
  public ScheduledTransactionResponse scheduleTransaction(
      @PathVariable UUID accountId, @RequestBody ScheduledTransactionRequest req) {
    return scheduleService.scheduleTransaction(accountId, req);
  }
}
