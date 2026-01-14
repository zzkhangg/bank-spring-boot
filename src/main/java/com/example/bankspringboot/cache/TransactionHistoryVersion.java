package com.example.bankspringboot.cache;

import java.util.UUID;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class TransactionHistoryVersion {
  private final Cache cache;

  public TransactionHistoryVersion(CacheManager cacheManager) {
    this.cache = cacheManager.getCache("transactionHistoryVersion");
  }

  public long get(UUID accountId) {
    Long version = cache.get(accountId, Long.class);
    return version == null ? 0 : version;
  }

  public void bump(UUID accountId) {
    Long version = cache.get(accountId, Long.class);
    cache.put(accountId, version == null ? 0 : version + 1);
  }
}
