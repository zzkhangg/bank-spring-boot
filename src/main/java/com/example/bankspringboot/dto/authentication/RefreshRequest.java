package com.example.bankspringboot.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshRequest {
  String refreshToken;
}
