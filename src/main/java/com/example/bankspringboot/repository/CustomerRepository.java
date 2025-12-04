package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.statistics.AdressStatisticsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
            SELECT c.address.city, count(c)
            from Customer c
            GROUP BY c.address.city
            """)
    List<AdressStatisticsDto> countCustomersByCity();
}
