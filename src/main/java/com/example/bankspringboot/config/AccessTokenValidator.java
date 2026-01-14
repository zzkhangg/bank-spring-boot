package com.example.bankspringboot.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenValidator implements OAuth2TokenValidator<Jwt> {

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    if (!"access".equals(jwt.getClaims().get("token_type"))) {
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid access token", "invalid access token", null));
    }

    return OAuth2TokenValidatorResult.success();
  }
}
