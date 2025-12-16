package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.service.CustomerService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  final private CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest req) {
    return customerService.createCustomer(req);
  }

  @GetMapping("/{id}")
  public CustomerResponse getCustomer(@PathVariable UUID id) {
    return customerService.getCustomer(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public CustomerResponse updateCustomer(@PathVariable UUID id,
      @Valid @RequestBody UpdateCustomerRequest req) {
    return customerService.updateCustomer(id, req);
  }

  @GetMapping("")
  public List<CustomerResponse> getAllCustomers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("Current User: {}", authentication.getName());
    authentication.getAuthorities()
        .forEach(authority
            -> log.info("Authorities: {}", authority.getAuthority()));
    return customerService.getAllCustomers();
  }
}
