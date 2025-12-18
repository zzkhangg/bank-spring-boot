package com.example.bankspringboot.service;

import com.example.bankspringboot.dto.AuthenticationRequest;
import com.example.bankspringboot.dto.AuthenticationResponse;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

  AuthenticationManager authenticationManager;

  @NonFinal
  @Value("${jwt.secret}")
  String SIGNER_KEY;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = generateToken(userDetails);
    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

  private String generateToken(UserDetails user) {
    JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(user.getUsername())
        .issuer("bankspringboot.example.com")
        .issueTime(new Date())
        .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .claim("scope", buildScope(user))
        .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(jwsHeader, payload);
    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private String buildScope(UserDetails user) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    user.getAuthorities()
        .forEach(grantedAuthority
            -> stringJoiner.add(grantedAuthority.getAuthority()));
    return stringJoiner.toString();
  }
}
