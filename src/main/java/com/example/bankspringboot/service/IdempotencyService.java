package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

  IdempotencyKeyRepository idempotencyKeyRepository;
  ObjectMapper objectMapper;
  IdempotencyKeyWriter idempotencyKeyWriter;
  ActionExecutor actionExecutor;

  @Transactional
  public <T> T handleIdempotency(
      String key, String requestHash, Class<T> responseType, Supplier<T> action)
      throws JsonProcessingException {
    try {
      log.error("HANDLE IDEMPOTENCY CALLED - key={}", key);
      IdempotencyKey idempotencyKey =
          IdempotencyKey.builder()
              .key(key)
              .requestHash(requestHash)
              .status(IdempotencyStatus.PROCESSING)
              .build();
      idempotencyKeyWriter.write(idempotencyKey);

      T response = actionExecutor.execute(action);
      idempotencyKeyWriter.markSuccess(key, objectMapper.writeValueAsString(response));

      return response;
    } catch (DataIntegrityViolationException e) {
      IdempotencyKey existing = idempotencyKeyRepository.findByKey(key).orElseThrow();

      if (existing.getStatus() == IdempotencyStatus.PROCESSING) {
        throw new AppException(ErrorCode.REQUEST_IN_PROGRESS, "Request is still processing");
      }
      return objectMapper.readValue(existing.getResponseBody(), responseType);
    }
  }
}
