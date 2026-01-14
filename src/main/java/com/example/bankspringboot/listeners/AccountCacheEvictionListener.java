package com.example.bankspringboot.listeners;

import com.example.bankspringboot.events.AccountCreatedEvent;
import com.example.bankspringboot.events.AccountDeletedEvent;
import com.example.bankspringboot.events.AccountStatusUpdatedEvent;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountCacheEvictionListener {
  private final CacheManager cacheManager;

  public AccountCacheEvictionListener(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onAccountCreated(AccountCreatedEvent event) {
    cacheManager.getCache("accountsByUser").evict(event.userId());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onAccountDeleted(AccountDeletedEvent event) {
    cacheManager.getCache("accountsByUser").evict(event.userId());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onAccountStatusUpdated(AccountStatusUpdatedEvent event) {
    cacheManager.getCache("accountsByUser").evict(event.userId());
  }
}
