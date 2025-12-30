package com.example.bankspringboot.config;


import com.example.bankspringboot.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvalidatedTokenValidator implements OAuth2TokenValidator<Jwt> {

  final private TokenValidationService tokenValidationService;

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    String jti = jwt.getId();
    try {
      tokenValidationService.verifyInvalidatedToken(jti);
      return OAuth2TokenValidatorResult.success();
    } catch (JwtException e) {
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid_token", "Token has been invalidated",
              null));
    }
  }
}
