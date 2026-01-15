package com.example.bankspringboot.repository;

import com.example.bankspringboot.common.CustomerTypeCode;
import com.example.bankspringboot.domain.customer.CustomerType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerTypeRepository extends JpaRepository<CustomerType, UUID> {
  Optional<CustomerType> findByCode(CustomerTypeCode code);
}
