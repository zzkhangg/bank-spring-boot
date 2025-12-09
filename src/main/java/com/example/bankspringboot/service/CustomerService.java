package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

  final private CustomerRepository customerRepository;
  final private PasswordEncoder passwordEncoder;

  public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest req) {
    Customer cus = new Customer(req.getFirstName(), req.getLastName(), req.getEmail(),
        req.getPhone(),
        req.getBirthdate(), this.passwordEncoder.encode(req.getPassword()), req.getAddress());
    Customer saved = customerRepository.save(cus);
    return new CustomerResponse(saved.getId(), saved.getFirstName(), saved.getLastName(),
        saved.getEmail(), saved.getPhone(), saved.getAddress());
  }

  public CustomerResponse getCustomer(Long id) {
    Customer cus = customerRepository.findById(id)
        .orElseThrow(() -> new IdInvalidException("Customer with id " + id + " not found"));
    return new CustomerResponse(cus.getId(), cus.getFirstName(), cus.getLastName(), cus.getEmail(),
        cus.getPhone(), cus.getAddress());
  }

  public List<CustomerResponse> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    List<CustomerResponse> customerResponseList = new ArrayList<>();
    customers.forEach(customer -> {
      customerResponseList.add(new CustomerResponse(customer.getId(), customer.getFirstName(),
          customer.getLastName(), customer.getEmail(), customer.getPhone(), customer.getAddress()));
    });
    return customerResponseList;
  }

  @Transactional
  public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest req) {
    Customer customer = customerRepository.findById(id).orElseThrow(
        () -> new IdInvalidException("Customer with id " + id + " not found"));

    customer.setFirstName(req.getFirstName());
    customer.setLastName(req.getLastName());
    customer.setEmail(req.getEmail());
    customer.setPhone(req.getPhone());
    customerRepository.save(customer);
    return new CustomerResponse(customer.getId(), customer.getFirstName(), customer.getLastName(),
        customer.getEmail(), customer.getPhone(), customer.getAddress());
  }

  public boolean existsCustomer(Long id) {
    return customerRepository.existsById(id);
  }

  @Transactional
  public void deleteCustomer(Long id) {
    try {
      customerRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      System.out.println("User not found.");
    }
  }
}