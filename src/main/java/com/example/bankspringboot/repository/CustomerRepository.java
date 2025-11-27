package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
