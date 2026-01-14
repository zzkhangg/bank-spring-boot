package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.authentication.AuthenticationRequest;
import com.example.bankspringboot.dto.authentication.AuthenticationResponse;
import com.example.bankspringboot.dto.authentication.LogoutRequest;
import com.example.bankspringboot.dto.authentication.RefreshRequest;
import com.example.bankspringboot.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

  AuthService authService;

  @PostMapping("/customer/login")
  public AuthenticationResponse authenticateUser(@RequestBody AuthenticationRequest request) {
    return authService.authenticate(request);
  }

  @PostMapping("admin/login")
  public AuthenticationResponse authenticateAdmin(@RequestBody AuthenticationRequest request) {
    return authService.authenticate(request);
  }

  @PostMapping("/logout")
  public void logout(@RequestBody LogoutRequest request) {
    authService.logout(request);
  }

  @PostMapping("/refresh")
  public AuthenticationResponse refreshToken(@RequestBody RefreshRequest request) {
    return authService.refreshToken(request);
  }
}
