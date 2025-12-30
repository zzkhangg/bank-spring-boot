package com.example.bankspringboot.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
  String refreshToken;
}
