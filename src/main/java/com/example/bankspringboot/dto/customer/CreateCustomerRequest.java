package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerRequest {

  @NotBlank(message = "First name is required")
  String firstName;

  @NotBlank(message = "Last name is required")
  String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  String email;

  @NotBlank(message = "Phone is required")
  @Size(min = 10, max = 10, message = "Phone Number must include exact 10 digits")
  String phoneNumber;

  @NotNull(message = "Birthdate is required")
  @Past(message = "Birthdate must be in the past")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  LocalDate birthdate;

  @NotNull
  @Size(min = 8, max = 30, message = "Enter password from 8 up to 30 characters")
  String password;

  @Valid
  Address address;
}