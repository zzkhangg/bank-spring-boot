package com.example.bankspringboot.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
  @NotBlank(message = "Email is required.")
  @Email(message = "Email must be a valid email address.")
  String email;

  @NotNull(message = "Password is required.")
  @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters.")
  String password;
}
