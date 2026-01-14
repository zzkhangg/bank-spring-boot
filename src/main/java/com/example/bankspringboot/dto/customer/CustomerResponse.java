package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.dto.role.RoleResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CustomerResponse {
  String firstName;
  String lastName;
  String email;
  String phoneNumber;
  Address address;
  String customerId;
  List<RoleResponse> roles;
}
