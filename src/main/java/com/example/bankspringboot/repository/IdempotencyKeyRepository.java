package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {
  Optional<IdempotencyKey> findByKey(String key);
}
