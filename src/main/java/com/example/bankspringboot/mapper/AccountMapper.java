package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  @Mapping(target = "accountId", expression = "java(account.getId().toString())")
  AccountResponse toResponse(Account account);

  @Mapping(target = "customer", ignore = true)
  @Mapping(source = "initialDeposit", target = "balance")
  Account toAccount(CreateAccountRequest request);

  @Mapping(target = "accountStatusHistory",  ignore = true)
  void updateAccountFromRequest(UpdateAccountRequest request, @MappingTarget Account account);

}
