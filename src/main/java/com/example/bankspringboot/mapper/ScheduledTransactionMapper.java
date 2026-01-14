package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionRequest;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ScheduledTransactionMapper {

  @Mapping(target = "toAccount", ignore = true)
  ScheduledTransaction toScheduledTransaction(ScheduledTransactionRequest request);

  @Mapping(target = "id", expression = "java(scheduledTransaction.getId().toString())")
  @Mapping(
      target = "toAccountId",
      expression = "java(scheduledTransaction.getToAccount().getId().toString())")
  @Mapping(target = "nextRunAt", source = ".", qualifiedByName = "toLocalDateTime")
  ScheduledTransactionResponse toResponse(ScheduledTransaction scheduledTransaction);

  DepositRequest toDepositRequest(ScheduledTransaction scheduledTransaction);

  @Mapping(target = "destinationAccountId", ignore = true)
  TransferRequest toTransferRequest(ScheduledTransaction scheduledTransaction);

  @Named(value = "toLocalDateTime")
  default LocalDateTime toLocalDateTime(ScheduledTransaction st) {
    return st.getNextRunAt().atZone(st.getZoneId()).toLocalDateTime();
  }
}
