package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.transaction.ScheduledTransactionRequest;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionResponse;
import com.example.bankspringboot.service.ScheduledTransactionService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/accounts/{accountId}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTransactionController {
  ScheduledTransactionService scheduleService;

  @PostMapping("/scheduled-transactions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ScheduledTransactionResponse scheduleTransaction(
      @PathVariable UUID accountId, @Valid @RequestBody ScheduledTransactionRequest req) {
    return scheduleService.scheduleTransaction(accountId, req);
  }
}
