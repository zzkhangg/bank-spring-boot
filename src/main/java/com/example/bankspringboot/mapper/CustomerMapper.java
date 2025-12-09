package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.customer.CreateCustomerRequest;
import com.example.bankspringboot.dto.customer.CustomerResponse;
import com.example.bankspringboot.dto.customer.UpdateCustomerRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
  // 1. Get the instance of the generated mapper
  CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

  // 2. Define the mapping method

  CustomerResponse toResponse(Customer user);

  Customer toCustomer(CreateCustomerRequest request);

  @Mapping(target = "id", ignore = true) // The ID is used for lookup, not for updating the entity's ID field
  void updateCustomerFromRequest(UpdateCustomerRequest request, @MappingTarget Customer user);

  List<CustomerResponse> toResponseList(List<Customer> customers);
}
