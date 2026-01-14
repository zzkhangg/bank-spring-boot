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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class IdempotencyService {

  IdempotencyKeyRepository idempotencyKeyRepository;
  ObjectMapper objectMapper;
  IdempotencyKeyWriter idempotencyKeyWriter;
  ActionExecutor actionExecutor;

  public <T> T handleIdempotency(
      String key, String requestHash, Class<T> responseType, Supplier<T> action)
      throws JsonProcessingException {
    try {
      IdempotencyKey idempotencyKey =
          IdempotencyKey.builder()
              .key(key)
              .requestHash(requestHash)
              .status(IdempotencyStatus.PROCESSING)
              .build();
      idempotencyKeyWriter.write(idempotencyKey);

      T response = actionExecutor.execute(action);
      idempotencyKey.setResponseBody(objectMapper.writeValueAsString(response));
      idempotencyKey.setStatus(IdempotencyStatus.SUCCESS);
      idempotencyKeyWriter.write(idempotencyKey);

      return response;
    } catch (DataIntegrityViolationException e) {
      IdempotencyKey idempotencyKey =
          idempotencyKeyRepository
              .findByKey(key)
              .orElseThrow(
                  () ->
                      new AppException(
                          ErrorCode.UNCATEGORIZED_EXCEPTION,
                          "Database error when fetching idempotency key table"));
      return objectMapper.readValue(idempotencyKey.getResponseBody(), responseType);
    }
  }
}
