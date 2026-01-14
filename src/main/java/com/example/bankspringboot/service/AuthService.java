package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.InvalidatedToken;
import com.example.bankspringboot.dto.authentication.AuthenticationRequest;
import com.example.bankspringboot.dto.authentication.AuthenticationResponse;
import com.example.bankspringboot.dto.authentication.LogoutRequest;
import com.example.bankspringboot.dto.authentication.RefreshRequest;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

  AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  @NonFinal
  @Value("${jwt.secret}")
  String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.valid-duration}")
  String VALIDATION_DURATION;

  @NonFinal
  @Value("${jwt.refreshable-duration}")
  String REFRESHABLE_DURATION;

  String REFRESH_TOKEN = "refresh";
  String ACCESS_TOKEN = "access";
  InvalidatedTokenRepository invalidatedTokenRepository;

  TokenValidationService tokenValidationService;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    String accessToken = generateAccessToken(userDetails);
    String refreshToken = generateRefreshToken(userDetails);
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  private String generateToken(UserDetails user, boolean isRefreshToken) {
    JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
    long duration = Long.parseLong(isRefreshToken ? REFRESHABLE_DURATION : VALIDATION_DURATION);
    JWTClaimsSet jwtClaimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .issuer("bankspringboot.example.com")
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS)))
            .claim("scope", buildScope(user))
            .jwtID(UUID.randomUUID().toString())
            .claim("token_type", isRefreshToken ? REFRESH_TOKEN : ACCESS_TOKEN)
            .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(jwsHeader, payload);
    try {
      jwsObject.sign(new MACSigner(Base64.getDecoder().decode(SIGNER_KEY)));
      return jwsObject.serialize();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public String generateAccessToken(UserDetails user) {
    return generateToken(user, false);
  }

  public String generateRefreshToken(UserDetails user) {
    return generateToken(user, true);
  }

  public SignedJWT verifyToken(String token, boolean isRefresh)
      throws JOSEException, ParseException {
    JWSVerifier jwsVerifier = new MACVerifier(Base64.getDecoder().decode(SIGNER_KEY));

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();
    String tokenType = signedJWT.getJWTClaimsSet().getStringClaim("token_type");

    boolean verified = signedJWT.verify(jwsVerifier);

    if (!verified) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid signature");
    }

    if (expiryDate.before(new Date())) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Token expired");
    }

    String jti = signedJWT.getJWTClaimsSet().getJWTID();
    if (jti == null) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Missing jti");
    }
    tokenValidationService.verifyInvalidatedToken(jti);

    if (isRefresh && !REFRESH_TOKEN.equals(tokenType)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid token type");
    }

    if (!isRefresh && !ACCESS_TOKEN.equals(tokenType)) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid token type");
    }

    return signedJWT;
  }

  public AuthenticationResponse refreshToken(RefreshRequest req) {
    try {
      SignedJWT refresh = verifyToken(req.getRefreshToken(), true);

      // Persist invalidated token
      invalidatedTokenRepository.save(
          InvalidatedToken.builder()
              .id(refresh.getJWTClaimsSet().getJWTID())
              .expiryDate(refresh.getJWTClaimsSet().getExpirationTime().toInstant())
              .build());

      UserDetails user =
          userDetailsService.loadUserByUsername(refresh.getJWTClaimsSet().getSubject());
      String accessToken = generateAccessToken(user);
      String refreshToken = generateRefreshToken(user);

      return AuthenticationResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .build();
    } catch (AppException | JOSEException | ParseException | JwtException e) {
      throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid token");
    }
  }

  public void logout(LogoutRequest request) {
    try {
      SignedJWT refresh = verifyToken(request.getRefreshToken(), true);

      // Persist invalidated token
      invalidatedTokenRepository.save(
          InvalidatedToken.builder()
              .id(refresh.getJWTClaimsSet().getJWTID())
              .expiryDate(refresh.getJWTClaimsSet().getExpirationTime().toInstant())
              .build());
    } catch (Exception e) {
      log.info("Logout failed or token already invalidated");
    }
  }

  private String buildScope(UserDetails user) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    user.getAuthorities()
        .forEach(grantedAuthority -> stringJoiner.add(grantedAuthority.getAuthority()));
    return stringJoiner.toString();
  }
}
