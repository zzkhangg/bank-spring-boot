package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerPasswordRequest;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest req) {
    return customerService.createCustomer(req);
  }

  @GetMapping("/{id}")
  public CustomerResponse getCustomer(@PathVariable UUID id) {
    return customerService.getCustomer(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}")
  public CustomerResponse updateCustomer(
      @PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest req) {
    return customerService.updateCustomer(id, req);
  }

  @PutMapping("/me/password")
  public CustomerResponse updatePassword(@Valid @RequestBody UpdateCustomerPasswordRequest request) {
    return customerService.updatePassword(request);
  }

  @GetMapping("")
  public List<CustomerResponse> getAllCustomers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("Current User: {}", authentication.getName());
    authentication
        .getAuthorities()
        .forEach(authority -> log.info("Authorities: {}", authority.getAuthority()));
    return customerService.getAllCustomers();
  }
}
