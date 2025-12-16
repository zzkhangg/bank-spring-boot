package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.mapper.CustomerMapper;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.exceptions.IdInvalidException;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

  final private CustomerRepository customerRepository;
  final private PasswordEncoder passwordEncoder;
  final private CustomerMapper customerMapper;

  public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
      CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
    this.customerMapper = customerMapper;
  }

  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest req) {
    Customer customer = customerMapper.toCustomer(req);

    customer.setPassword(passwordEncoder.encode(req.getPassword()));
      Customer saved = customerRepository.save(customer);
      return  customerMapper.toResponse(saved);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public CustomerResponse getCustomer(UUID id) {
    Customer cus = customerRepository.findById(id)
        .orElseThrow(() -> new IdInvalidException("Customer with id " + id + " not found"));
    return customerMapper.toResponse(cus);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<CustomerResponse> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return customerMapper.toResponseList(customers);
  }

  @Transactional
  public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest req) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IdInvalidException("Customer with id " + id + " not found"));
    customerMapper.updateCustomerFromRequest(req, customer);

    Customer updated = customerRepository.save(customer);
    return customerMapper.toResponse(updated);
  }

  @Transactional
  public void deleteCustomer(UUID id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IdInvalidException("Customer with id " + id + " not found"));
    customerRepository.delete(customer);
  }
}