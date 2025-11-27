package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CreateCustomerRequest req) {
        Customer cus = new Customer(req.getFname(), req.getLname(), req.getEmail(), req.getPhone(), req.getBirthdate());
        Customer saved = customerRepository.save(cus);
        return new CustomerResponse(saved.getId(), saved.getFname(), saved.getLname(), saved.getEmail(), saved.getPhone());
    }

}