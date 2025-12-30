package com.example.bankspringboot.service;

import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenValidationService {
  InvalidatedTokenRepository invalidatedTokenRepository;

  public void verifyInvalidatedToken(String jti) {
    if (invalidatedTokenRepository.existsById(jti)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid token");
    }
  }
}
