package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCustomerRequest {

  String firstName;

  String lastName;

  @Email(message = "Email should be valid")
  String email;

  @Size(min = 10, max = 10, message = "Phone Number must include exact 10 digits")
  String phone;

  @Past(message = "Birthdate must be in the past")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  LocalDate birthdate;

  @Size(min = 8, max = 30, message = "Enter password from 8 up to 30 characters")
  String password;

  @Valid Address address;
}
