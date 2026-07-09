package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.account.AccountStatusHistory;
import com.example.bankspringboot.dto.account.AccountStatusHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountStatusHistoryMapper {

  @Mapping(source = "changedAt", target = "modifiedAt")
  @Mapping(source = "changedBy", target = "modifiedBy")
  AccountStatusHistoryResponse toResponse(AccountStatusHistory history);
}
