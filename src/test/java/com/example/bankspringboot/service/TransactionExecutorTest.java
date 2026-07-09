package com.example.bankspringboot.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.events.TransactionCreatedEvent;
import com.example.bankspringboot.mapper.TransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class TransactionExecutorTest {
  @Mock private TransactionRepository transactionRepository;

  @Mock private AccountRepository accountRepository;

  private TransactionMapper transactionMapper;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private TransactionExecutor transactionExecutor;

  @BeforeEach
  void setUp() {
    transactionMapper = Mappers.getMapper(TransactionMapper.class);

    transactionExecutor =
            new TransactionExecutor(
                    transactionRepository,
                    accountRepository,
                    transactionMapper,
                    eventPublisher
            );
  }

  @Test
  public void deposit_shouldIncreaseBalance() {
    UUID accountId = UUID.randomUUID();
    DepositRequest request = depositRequest();
    Account account = activeAccount(accountId, customer());
    account.setBalance(BigDecimal.valueOf(500));

    when(accountRepository.findByIdWithPessimisticLock(accountId)).thenReturn(Optional.of(account));
    when(transactionRepository.save(any(Transaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    TransactionResponse response = transactionExecutor.executeDeposit(accountId, request);

    assertThat(account.getBalance()).isEqualByComparingTo("1500");
    assertThat(response.getAmount()).isEqualByComparingTo("1000");
    assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

    verify(accountRepository).findByIdWithPessimisticLock(accountId);
    verify(transactionRepository).save(any(Transaction.class));
    verify(eventPublisher).publishEvent(any(TransactionCreatedEvent.class));
  }

  private DepositRequest depositRequest() {
    return DepositRequest.builder()
        .amount(BigDecimal.valueOf(1000))
        .description("chuyen tien cho Khang")
        .currency("USD")
        .address(
            Address.builder()
                .street("Van Coi Street")
                .city("Ho Chi Minh City")
                .postalCode("70000")
                .country("Viet Nam")
                .build())
        .channel(TransactionChannel.ATM)
        .build();
  }

  private Customer customer() {
    return Customer.builder()
        .id(UUID.randomUUID())
        .email("vuongkhang2005@gmail.com")
        .password("password")
        .firstName("Khang")
        .lastName("Vuong")
        .phoneNumber("0123456789")
        .birthDate(LocalDate.of(2005, 1, 1))
        .address(
            Address.builder()
                .street("Van Coi Street")
                .city("Ho Chi Minh City")
                .postalCode("70000")
                .country("Viet Nam")
                .build())
        .build();
  }

  private Account activeAccount(UUID accountId, Customer customer) {
    return Account.builder()
        .id(accountId)
        .accountNumber("1234567890")
        .balance(BigDecimal.ZERO)
        .transactionLimit(BigDecimal.valueOf(100000))
        .createdAt(LocalDateTime.now())
        .customer(customer)
        .status(AccountStatus.ACTIVE)
        .accountType(AccountType.SAVINGS)
        .build();
  }
}
