package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateCustomerRequest {

  @NotBlank(message = "First name is required.")
  String firstName;

  @NotBlank(message = "Last name is required.")
  String lastName;

  @NotBlank(message = "Email is required.")
  @Email(message = "Email must be a valid email address.")
  String email;

  @Pattern(
          regexp = "^\\d{10}$",
          message = "Phone number must contain exactly 10 digits."
  )
  String phoneNumber;

  @NotNull(message = "Birthdate is required.")
  @Past(message = "Birthdate must be in the past.")
  @JsonProperty("birthDate")
  LocalDate birthdate;

  @NotNull(message = "Password is required.")
  @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters.")
  String password;

  @Valid Address address;
}
