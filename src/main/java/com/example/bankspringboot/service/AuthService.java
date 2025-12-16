package com.example.bankspringboot.service;

import com.example.bankspringboot.common.Role;
import com.example.bankspringboot.domain.admin.Admin;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.response.RestResponse;
import com.example.bankspringboot.dto.AuthenticationRequest;
import com.example.bankspringboot.dto.AuthenticationResponse;
import com.example.bankspringboot.repository.AdminRespository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

  CustomerRepository customerRepository;
  private final AdminRespository adminRespository;

  @NonFinal
  @Value("${jwt.secret}")
  String SIGNER_KEY;

  PasswordEncoder passwordEncoder;

  public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
    Customer customer = customerRepository.findByEmail(request.getEmail()).orElseThrow(
        () -> new RuntimeException("Customer with email " + request.getEmail() + " not found"));
    String token = generateToken(customer);
    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

  public AuthenticationResponse authenticateAdmin(AuthenticationRequest request) {
    Admin admin = adminRespository.findByUsername(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Admin not found"));
    String token = generateToken(admin);
    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

  private String generateToken(Customer customer) {
    JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(customer.getEmail())
        .issuer("bankspringboot.example.com")
        .issueTime(new Date())
        .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        .claim("scope", Role.USER.name())
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
    private String generateToken(Admin admin) {
      JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
      JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
          .subject(admin.getUsername())
          .issuer("bankspringboot.example.com")
          .issueTime(new Date())
          .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
          .claim("scope", Role.ADMIN.name())
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
}
