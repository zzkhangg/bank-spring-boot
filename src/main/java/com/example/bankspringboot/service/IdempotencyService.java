package com.example.bankspringboot.service;

import java.util.function.Supplier;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

  IdempotencyKeyRepository idempotencyKeyRepository;
  ObjectMapper objectMapper;
  IdempotencyKeyWriter idempotencyKeyWriter;
  IdempotentActionExecutor actionExecutor;

  public <T> T handleIdempotency(
      String key, String requestHash, Class<T> responseType, Supplier<T> action)
      throws JsonProcessingException {
      IdempotencyKey idempotencyKey =
          IdempotencyKey.builder()
              .key(key)
              .requestHash(requestHash)
              .status(IdempotencyStatus.PROCESSING)
              .build();
      boolean claimed;

      try {
          idempotencyKeyWriter.write(idempotencyKey);
          claimed = true;
      } catch (DataIntegrityViolationException e) {
          claimed = false;
      }

      if (!claimed) {
        return handleExistingKey(key, requestHash, responseType);
      }

      try {
      T response = actionExecutor.execute(
          key,
          action,
          result -> {
              try {
                  return objectMapper.writeValueAsString(result);
              } catch (JsonProcessingException e) {
                  throw new IllegalStateException(e);
              }
          }
      );

      return response;
      } catch (Exception e) {
          idempotencyKeyWriter.markFailed(key);
          throw e;
      }
  }

  public <T> T handleExistingKey(String key, String requestHash, Class<T> responseType) throws JsonProcessingException {
    IdempotencyKey existing = idempotencyKeyRepository.findByKey(key).orElseThrow();

    if (!existing.getRequestHash().equals(requestHash)) {
      throw new AppException(ErrorCode.IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_REQUEST);
    }

    if (existing.getStatus() == IdempotencyStatus.PROCESSING) {
      throw new AppException(ErrorCode.REQUEST_IN_PROGRESS, "Request is still processing");
    }

    if (existing.getStatus() == IdempotencyStatus.FAILED) {
      throw new AppException(ErrorCode.REQUEST_FAILED, "Request failed.");
    }

    return objectMapper.readValue(existing.getResponseBody(), responseType);
  }
}
