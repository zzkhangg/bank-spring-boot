package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
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
  @Mapping(target = "transactionId", expression = "java(transaction.getId().toString())")
  TransactionResponse toResponse(Transaction transaction);

  List<TransactionResponse> toResponseList(List<Transaction> transactions);

  @Mapping(target = "account", ignore = true)
  Transaction depositReqToTransaction(DepositRequest depositRequest);

  Transaction withdrawalReqToTransaction(WithdrawalRequest withdrawalRequest);

  @Mapping(target = "account", ignore = true) // Ignore relationship
  @Mapping(target = "customer", ignore = true) // Ignore relationship
  @Mapping(target = "id", ignore = true)
  Transaction transferReqToTransaction(TransferRequest transferRequest);


}
