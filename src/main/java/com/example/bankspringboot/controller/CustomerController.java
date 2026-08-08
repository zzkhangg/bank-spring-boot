package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerPasswordRequest;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

  @GetMapping("/me")
  public CustomerResponse getCurrentCustomer() {
    return customerService.getCurrentCustomer();
  }

  @PatchMapping("/me")
  public CustomerResponse updateCustomer(@Valid @RequestBody UpdateCustomerRequest req) {
    return customerService.updateCurrentCustomer(req);
  }

  @PutMapping("/me/password")
  public CustomerResponse updatePassword(@Valid @RequestBody UpdateCustomerPasswordRequest request) {
    return customerService.updatePassword(request);
  }
}
