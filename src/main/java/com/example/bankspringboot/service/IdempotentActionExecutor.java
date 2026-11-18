package com.example.bankspringboot.service;

import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.repository.IdempotencyKeyRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor 
public class IdempotentActionExecutor {

  private final IdempotencyKeyRepository idempotencyKeyRepository;

  @Transactional
    public <T> T execute(
            String key,
            Supplier<T> action,
            Function<T, String> serializer) {

        T response = action.get();

        IdempotencyKey idempotencyKey =
                idempotencyKeyRepository.findByKey(key).orElseThrow();

        idempotencyKey.setStatus(IdempotencyStatus.SUCCESS);
        idempotencyKey.setResponseBody(serializer.apply(response));

        return response;
    }
}
