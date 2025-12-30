package com.example.bankspringboot.dto.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthenticationResponse {
  String accessToken;
  String refreshToken;
}
