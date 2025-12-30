package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.account.AccountStatusHistory;
import com.example.bankspringboot.dto.account.AccountStatusHistoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountStatusHistoryMapper {

  public AccountStatusHistoryResponse toResponse(AccountStatusHistory history);

}
