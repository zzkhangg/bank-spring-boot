package com.example.bankspringboot.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
  @NotBlank(message = "Refresh token is required.")
  String refreshToken;
}
