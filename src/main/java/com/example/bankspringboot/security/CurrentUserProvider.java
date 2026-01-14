package com.example.bankspringboot.security;

import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.CustomerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

  private final CustomerRepository customerRepository;

  public UUID getUserId() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return customerRepository
        .findByEmail(email)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND))
        .getId();
  }
}
