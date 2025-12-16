package com.example.bankspringboot.controller;

import com.example.bankspringboot.domain.response.RestResponse;
import com.example.bankspringboot.dto.AuthenticationRequest;
import com.example.bankspringboot.dto.AuthenticationResponse;
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
    return authService.authenticateUser(request);
  }

  @PostMapping("admin/login")
  public AuthenticationResponse authenticateAdmin(@RequestBody AuthenticationRequest request) {
    return authService.authenticateAdmin(request);
  }
}
