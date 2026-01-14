package com.example.bankspringboot.service;

import com.example.bankspringboot.common.Role;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.CustomerMapper;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

  CustomerRepository customerRepository;
  PasswordEncoder passwordEncoder;
  CustomerMapper customerMapper;
  RoleRepository roleRepository;

  Role DEFAULT_ROLE = Role.CUSTOMER;

  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest req) {
    Customer customer = customerMapper.toCustomer(req);
    com.example.bankspringboot.domain.Role role = roleRepository.findById(DEFAULT_ROLE.toString()).orElseThrow();
    customer.setRoles(Set.of(role));

    customer.setPassword(passwordEncoder.encode(req.getPassword()));
    customer.setBirthDate(req.getBirthdate());
    Customer saved = customerRepository.save(customer);
    return customerMapper.toResponse(saved);
  }

  @PreAuthorize(
      "hasRole('ADMIN') or" + "@customerSecurity.isValidCustomer(authentication.name, #id)")
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public CustomerResponse getCustomer(UUID id) {
    Customer cus =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    return customerMapper.toResponse(cus);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<CustomerResponse> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return customers.stream().map(customerMapper::toResponse).toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest req) {
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    customerMapper.updateCustomer(req, customer);

    Customer updated = customerRepository.save(customer);
    return customerMapper.toResponse(updated);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteCustomer(UUID id) {
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    customerRepository.delete(customer);
  }
}
