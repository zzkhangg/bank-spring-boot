package com.example.bankspringboot;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bankspringboot.common.CustomerStatus;
import com.example.bankspringboot.common.CustomerTypeCode;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.customer.CustomerType;
import com.example.bankspringboot.domain.idempotency.IdempotencyKey;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.CustomerTypeRepository;
import com.example.bankspringboot.repository.IdempotencyKeyRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.IdempotencyService;
import com.example.bankspringboot.service.TransactionExecutor;
import com.example.bankspringboot.util.HashUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class IdempotencyConcurrencyIntegrationTest extends BaseIntegrationTest {

  private static final String IDEMPOTENCY_KEY = "concurrent-transfer-key";
  private static final String REQUEST_HASH = HashUtil.sha256("source-to-destination-100-usd");

  @Autowired IdempotencyService idempotencyService;
  @Autowired TransactionExecutor transactionExecutor;
  @Autowired IdempotencyKeyRepository idempotencyKeyRepository;
  @Autowired TransactionRepository transactionRepository;
  @Autowired AccountRepository accountRepository;
  @Autowired CustomerRepository customerRepository;
  @Autowired CustomerTypeRepository customerTypeRepository;

  private Account sourceAccount;
  private Account destinationAccount;
  private TransferRequest request;

  @BeforeEach
  void setUp() {
    transactionRepository.deleteAll();
    idempotencyKeyRepository.deleteAll();
    accountRepository.deleteAll();
    customerRepository.deleteAll();

    CustomerType customerType =
        customerTypeRepository
            .findByCode(CustomerTypeCode.INDIVIDUAL)
            .orElseGet(
                () ->
                    customerTypeRepository.saveAndFlush(
                        CustomerType.builder()
                            .code(CustomerTypeCode.INDIVIDUAL)
                            .maxTransactionLimit(new BigDecimal("50000000.00"))
                            .build()));

    Customer sourceCustomer =
        customerRepository.saveAndFlush(
            customer("idempotency-source@test.com", "0900000011", customerType));
    Customer destinationCustomer =
        customerRepository.saveAndFlush(
            customer("idempotency-destination@test.com", "0900000012", customerType));

    sourceAccount =
        accountRepository.saveAndFlush(
            account("3333333333", new BigDecimal("1000.00"), sourceCustomer));
    destinationAccount =
        accountRepository.saveAndFlush(
            account("4444444444", new BigDecimal("100.00"), destinationCustomer));

    request =
        TransferRequest.builder()
            .destinationAccountId(destinationAccount.getId())
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .description("Concurrent idempotency test")
            .channel(TransactionChannel.ONLINE)
            .build();
  }

  @AfterEach
  void tearDown() {
    transactionRepository.deleteAll();
    idempotencyKeyRepository.deleteAll();
    accountRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Test
  void sameKeyWhileTransferIsProcessing_shouldExecuteTransferOnlyOnce() throws Exception {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    CountDownLatch actionStarted = new CountDownLatch(1);
    CountDownLatch releaseAction = new CountDownLatch(1);
    AtomicInteger actionInvocationCount = new AtomicInteger();

    Future<TransactionResponse> firstRequest =
        pool.submit(
            () ->
                idempotencyService.handleIdempotency(
                    IDEMPOTENCY_KEY,
                    REQUEST_HASH,
                    TransactionResponse.class,
                    () -> {
                      actionInvocationCount.incrementAndGet();
                      actionStarted.countDown();
                      await(releaseAction);
                      return transactionExecutor.executeTransfer(sourceAccount.getId(), request);
                    }));

    AppException duplicateFailure = null;
    TransactionResponse firstResponse;
    try {
      assertThat(actionStarted.await(10, TimeUnit.SECONDS)).isTrue();

      try {
        idempotencyService.handleIdempotency(
            IDEMPOTENCY_KEY,
            REQUEST_HASH,
            TransactionResponse.class,
            () -> {
              actionInvocationCount.incrementAndGet();
              return transactionExecutor.executeTransfer(sourceAccount.getId(), request);
            });
      } catch (AppException exception) {
        duplicateFailure = exception;
      }
    } finally {
      releaseAction.countDown();
    }

    try {
      firstResponse = firstRequest.get(10, TimeUnit.SECONDS);
    } finally {
      pool.shutdownNow();
    }

    Account persistedSource = accountRepository.findById(sourceAccount.getId()).orElseThrow();
    Account persistedDestination =
        accountRepository.findById(destinationAccount.getId()).orElseThrow();
    IdempotencyKey persistedKey =
        idempotencyKeyRepository.findByKey(IDEMPOTENCY_KEY).orElseThrow();

    assertThat(duplicateFailure).isNotNull();
    assertThat(duplicateFailure.getErrorCode()).isEqualTo(ErrorCode.REQUEST_IN_PROGRESS);
    assertThat(actionInvocationCount).hasValue(1);
    assertThat(firstResponse).isNotNull();
    assertThat(persistedSource.getBalance()).isEqualByComparingTo("899.00");
    assertThat(persistedDestination.getBalance()).isEqualByComparingTo("200.00");
    assertThat(transactionRepository.count()).isEqualTo(2);
    assertThat(idempotencyKeyRepository.count()).isEqualTo(1);
    assertThat(persistedKey.getStatus()).isEqualTo(IdempotencyStatus.SUCCESS);
    assertThat(persistedKey.getResponseBody()).isNotBlank();
  }

  private Customer customer(String email, String phoneNumber, CustomerType customerType) {
    return Customer.builder()
        .email(email)
        .password("encoded-password")
        .firstName("Idempotency")
        .lastName("Customer")
        .phoneNumber(phoneNumber)
        .birthDate(LocalDate.of(1995, 1, 1))
        .status(CustomerStatus.ACTIVE)
        .customerType(customerType)
        .address(
            Address.builder()
                .street("1 Integration Street")
                .city("Ho Chi Minh City")
                .postalCode("700000")
                .country("Vietnam")
                .build())
        .build();
  }

  private Account account(String accountNumber, BigDecimal balance, Customer customer) {
    return Account.builder()
        .accountNumber(accountNumber)
        .balance(balance)
        .transactionLimit(new BigDecimal("10000.00"))
        .customer(customer)
        .status(AccountStatus.ACTIVE)
        .accountType(AccountType.SAVINGS)
        .build();
  }

  private void await(CountDownLatch latch) {
    try {
      if (!latch.await(10, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Timed out waiting to release transfer action");
      }
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting to release transfer action", exception);
    }
  }
}
