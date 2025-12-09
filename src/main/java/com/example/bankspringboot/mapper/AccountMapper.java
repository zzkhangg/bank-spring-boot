package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  AccountResponse toResponse(Account account);

  @Mapping(target = "customer", ignore = true)
  @Mapping(source = "initialDeposit", target = "balance")
  Account toAccount(CreateAccountRequest request);

  @Mapping(target = "id", ignore = true)
    // The ID is used for lookup, not for updating the entity's ID field
  void updateAccountFromRequest(UpdateAccountRequest request, @MappingTarget Account account);
}
