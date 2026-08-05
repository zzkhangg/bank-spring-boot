package com.example.bankspringboot.config;

import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

import com.example.bankspringboot.config.properties.JwtProperties;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {
  private final NimbusJwtDecoder jwtDecoder;

  public CustomJwtDecoder(
      JwtProperties jwtProperties,
      InvalidatedTokenValidator invalidatedTokenValidator,
      AccessTokenValidator accessTokenValidator) {
    SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(jwtProperties.secret()), "HmacSHA512");

    this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS512).build();

    OAuth2TokenValidator<Jwt> combinedValidator =
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(jwtProperties.issuer()), accessTokenValidator, invalidatedTokenValidator);

    this.jwtDecoder.setJwtValidator(combinedValidator);
  }

  @Override
  public Jwt decode(String token) throws JwtException {
    return jwtDecoder.decode(token);
  }
}
