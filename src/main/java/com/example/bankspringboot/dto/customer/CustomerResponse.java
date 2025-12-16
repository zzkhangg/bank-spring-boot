package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
  String firstName;
  String lastName;
  String email;
  String phone;
  Address address;
  String customerId;
}
