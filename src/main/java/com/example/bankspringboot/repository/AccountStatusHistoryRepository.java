package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.account.AccountStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, UUID> {
  List<AccountStatusHistory> findAllByAccountIdOrderByChangedAtDesc(UUID id);
}
