package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import com.example.bankspringboot.dto.customer.UpdateCustomerTypeRequest;
import com.example.bankspringboot.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {
    private final CustomerService customerService;

    public AdminCustomerController(CustomerService customerService) {
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

    @PatchMapping("/{id}/customer-type")
    public  CustomerResponse updateCustomerType(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerTypeRequest req) {
        return customerService.updateCustomerType(id, req);
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
