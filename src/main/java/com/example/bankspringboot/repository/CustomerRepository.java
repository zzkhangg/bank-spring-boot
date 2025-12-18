package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.statistics.AddressStatisticsDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  @Query("""
      SELECT c.address.city, count(c)
      from Customer c
      GROUP BY c.address.city
      """)
  List<AddressStatisticsDto> countCustomersByCity();
  Optional<Customer> findByEmail(String email);
}
