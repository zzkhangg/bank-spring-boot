package com.example.bankspringboot.security;

import com.example.bankspringboot.repository.CustomerRepository;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerSecurity {
  CustomerRepository customerRepository;

  public boolean isValidCustomer(String email, UUID id) {
    return customerRepository.existsByIdAndEmail(id, email);
  }
}
