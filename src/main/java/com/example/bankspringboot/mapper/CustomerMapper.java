package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import java.util.Locale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = Locale.class)
public interface CustomerMapper {

  // 2. Define the mapping method
  @Mapping(target = "customerId", expression = "java(customer.getId().toString())")
  CustomerResponse toResponse(Customer customer);

  @Mapping(
      target = "email",
      expression = "java(request.getEmail().trim().toLowerCase(Locale.ROOT))")
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "id", ignore = true)
  Customer toCustomer(CreateCustomerRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  void updateCustomer(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
