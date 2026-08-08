package com.example.bankspringboot.service;

import com.example.bankspringboot.common.CustomerStatus;
import com.example.bankspringboot.common.CustomerTypeCode;
import com.example.bankspringboot.common.Role;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.customer.CustomerType;
import com.example.bankspringboot.dto.customer.*;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.CustomerMapper;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.CustomerTypeRepository;
import com.example.bankspringboot.repository.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.bankspringboot.security.CurrentUserProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

  CustomerRepository customerRepository;
  PasswordEncoder passwordEncoder;
  CustomerMapper customerMapper;
  RoleRepository roleRepository;

  Role DEFAULT_ROLE = Role.CUSTOMER;
  CustomerTypeRepository customerTypeRepository;
  CurrentUserProvider currentUserProvider;

  @Transactional
  public CustomerResponse createCustomer(CreateCustomerRequest req) {
    Customer customer = customerMapper.toCustomer(req);
    com.example.bankspringboot.domain.Role role =
        roleRepository.findById(DEFAULT_ROLE.toString()).orElseThrow();
    CustomerType customerType =
        customerTypeRepository.findByCode(CustomerTypeCode.INDIVIDUAL).orElseThrow();
    customer.setRoles(Set.of(role));
    customer.setCustomerType(customerType);
    customer.setPassword(passwordEncoder.encode(req.getPassword()));
    customer.setBirthDate(req.getBirthdate());


    Customer saved = customerRepository.save(customer);
    return customerMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public CustomerResponse getCustomer(UUID id) {
    Customer cus =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    return customerMapper.toResponse(cus);
  }

  @Transactional(readOnly = true)
  public CustomerResponse getCurrentCustomer() {
    return getCustomer(currentUserProvider.getUserId());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true)
  public List<CustomerResponse> getAllCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return customers.stream().map(customerMapper::toResponse).toList();
  }

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

  @Transactional
  public CustomerResponse updateCurrentCustomer(UpdateCustomerRequest req) {
    return updateCustomer(currentUserProvider.getUserId(), req);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteCustomer(UUID id) {
    Customer customer =
        customerRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    customer.setStatus(CustomerStatus.CLOSED);
  }

  @Transactional
  public CustomerResponse updateCustomerType(UUID id, UpdateCustomerTypeRequest request) {
    CustomerType customerType = customerTypeRepository.findByCode(request.getCustomerType()).orElseThrow();

    Customer customer =
            customerRepository
                    .findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

    customer.setCustomerType(customerType);

    return customerMapper.toResponse(customer);
  }

  @Transactional
  public CustomerResponse updatePassword(UpdateCustomerPasswordRequest request) {
    Customer customer = currentUserProvider.getCurrentCustomer();

    if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
      throw new AppException(
              ErrorCode.INVALID_CREDENTIALS,
              "Current password is incorrect"
      );
    }

    if (passwordEncoder.matches(
            request.getNewPassword(),
            customer.getPassword())) {

      throw new AppException(
              ErrorCode.SAME_PASSWORD);
    }

    customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
    return customerMapper.toResponse(customer);
  }
}
