package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCustomerRequest {

  @NotBlank(message = "First name is required")
  String firstName;

  @NotBlank(message = "Last name is required")
  String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  String email;

  @NotBlank(message = "Phone is required")
  String phone;

  @Valid
  Address address;
}