package com.example.bankspringboot.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshRequest {
  @NotBlank(message = "Refresh token is required.")
  String refreshToken;
}
