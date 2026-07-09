package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

  @Mapping(source = "type", target = "transactionType")
  @Mapping(target = "transactionId", expression = "java(transaction.getId())")
  @Mapping(target = "accountId", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  TransactionResponse toResponse(Transaction transaction);

  @Mapping(target = "account", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fee", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "type", ignore = true)
  Transaction depositReqToTransaction(DepositRequest depositRequest);

  @Mapping(target = "account", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fee", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "type", ignore = true)
  Transaction withdrawalReqToTransaction(WithdrawalRequest withdrawalRequest);

  @Mapping(target = "account", ignore = true) // Ignore relationship
  @Mapping(target = "customer", ignore = true) // Ignore relationship
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "fee", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "type", ignore = true)
  Transaction transferReqToTransaction(TransferRequest transferRequest);
}
