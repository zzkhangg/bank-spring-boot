package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class CreateCustomerRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotBlank(message = "Phone is required")
  @Size(min = 10, max = 10, message = "Phone Number must include exact 10 digits")
  private String phone;

  @NotNull(message = "Birthdate is required")
  @Past(message = "Birthdate must be in the past")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthdate;

  @NotNull
  @Size(min = 8, max = 30, message = "Enter password from 8 up to 30 characters")
  private String password;

  @Valid
  private Address address;
}