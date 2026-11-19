package com.example.bankspringboot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.example.bankspringboot.common.CustomerStatus;
import com.example.bankspringboot.common.CustomerTypeCode;
import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.config.CustomerTypeConfig;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.customer.CustomerType;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.CustomerTypeRepository;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.ScheduledExecutionClaimer;
import com.example.bankspringboot.service.ScheduledExecutionFailureHandler;
import com.example.bankspringboot.service.ScheduledTransactionExecutor;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.hibernate.tool.schema=DEBUG",
    "spring.jpa.show-sql=true"
})
public class ScheduledTransactionIntegrationTest extends BaseIntegrationTest {
    @Autowired ScheduledExecutionClaimer claimer;
    @Autowired ScheduledTransactionExecutor executor;
    @Autowired ScheduledExecutionFailureHandler failureHandler;

    @Autowired ScheduledTransactionRepository scheduleRepository;
    @Autowired ScheduledTransactionExecutionRepository executionRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired TransactionRepository transactionRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired CustomerTypeRepository customerTypeRepository;
    @Autowired CustomerTypeConfig customerTypeConfig;

    @Autowired EntityManager entityManager;


    private static final ZoneId TEST_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private Customer sourceCustomer;
    private Customer destinationCustomer;

    private Account sourceAccount;
    private Account destinationAccount;

    private ScheduledTransaction schedule;
    private Instant intendedRunAt;

    @BeforeEach
    void setUp() {
        // Xóa dữ liệu test cũ theo thứ tự tránh foreign-key violation.
        transactionRepository.deleteAll();
        executionRepository.deleteAll();
        scheduleRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();

        // CustomerType có thể đã được ApplicationInitConfig tạo.
        CustomerType customerType =
            customerTypeRepository
                .findByCode(CustomerTypeCode.INDIVIDUAL)
                .orElseGet(
                    () ->
                        customerTypeRepository.saveAndFlush(
                            CustomerType.builder()
                                .code(CustomerTypeCode.INDIVIDUAL)
                                .maxTransactionLimit(
                                    new BigDecimal("50000000.00")
                                )
                                .build()
                        )
                );

        Address sourceAddress =
            Address.builder()
                .street("1 Source Street")
                .city("Ho Chi Minh City")
                .postalCode("700000")
                .country("Vietnam")
                .build();

        Address destinationAddress =
            Address.builder()
                .street("2 Destination Street")
                .city("Ho Chi Minh City")
                .postalCode("700000")
                .country("Vietnam")
                .build();

        sourceCustomer =
            customerRepository.saveAndFlush(
                Customer.builder()
                    .email("source@test.com")
                    .password("encoded-password")
                    .firstName("Source")
                    .lastName("Customer")
                    .phoneNumber("0900000001")
                    .birthDate(LocalDate.of(1995, 1, 1))
                    .status(CustomerStatus.ACTIVE)
                    .customerType(customerType)
                    .address(sourceAddress)
                    .build()
            );

        destinationCustomer =
            customerRepository.saveAndFlush(
                Customer.builder()
                    .email("destination@test.com")
                    .password("encoded-password")
                    .firstName("Destination")
                    .lastName("Customer")
                    .phoneNumber("0900000002")
                    .birthDate(LocalDate.of(1996, 1, 1))
                    .status(CustomerStatus.ACTIVE)
                    .customerType(customerType)
                    .address(destinationAddress)
                    .build()
            );

        sourceAccount =
            accountRepository.saveAndFlush(
                Account.builder()
                    .accountNumber("1111111111")
                    .balance(new BigDecimal("1000.00"))
                    .transactionLimit(new BigDecimal("10000.00"))
                    .customer(sourceCustomer)
                    .status(AccountStatus.ACTIVE)
                    .accountType(AccountType.SAVINGS)
                    .build()
            );

        destinationAccount =
            accountRepository.saveAndFlush(
                Account.builder()
                    .accountNumber("2222222222")
                    .balance(new BigDecimal("100.00"))
                    .transactionLimit(new BigDecimal("10000.00"))
                    .customer(destinationCustomer)
                    .status(AccountStatus.ACTIVE)
                    .accountType(AccountType.SAVINGS)
                    .build()
            );

        intendedRunAt = Instant.now()
            .minusSeconds(10)
            .truncatedTo(ChronoUnit.MICROS);

        schedule =
            scheduleRepository.saveAndFlush(
                ScheduledTransaction.builder()
                    .amount(new BigDecimal("100.00"))
                    .currency("USD")
                    .description("Integration test transfer")
                    .transactionType(TransactionType.TRANSFER_OUT)
                    .fromAccount(sourceAccount)
                    .toAccount(destinationAccount)
                    .scheduledType(ScheduledType.ONCE)
                    .startAtLocal(
                        LocalDateTime.ofInstant(
                            intendedRunAt,
                            TEST_ZONE
                        ).truncatedTo(ChronoUnit.MICROS)
                    )
                    .zoneId(TEST_ZONE)
                    .nextRunAt(intendedRunAt)
                    .status(ScheduledStatus.ACTIVE)
                    .build()
            );

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        executionRepository.deleteAll();
        scheduleRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void execute_shouldCommitMoneyLedgerScheduleAndExecutionTogether() {
        UUID executionId = claimer.claim(schedule.getId(), intendedRunAt);

        executor.execute(executionId);
        entityManager.clear();

        Account persistedSource =
            accountRepository.findById(sourceAccount.getId()).orElseThrow();

        Account persistedDestination =
            accountRepository.findById(destinationAccount.getId()).orElseThrow();

        ScheduledTransaction persistedSchedule =
            scheduleRepository.findById(schedule.getId()).orElseThrow();

        ScheduledTransactionExecution persistedExecution =
            executionRepository.findById(executionId).orElseThrow();

        assertThat(persistedSource.getBalance())
            .isEqualByComparingTo("899.00"); // tùy fee của project

        assertThat(persistedDestination.getBalance())
            .isEqualByComparingTo("200.00");

        assertThat(persistedExecution.getStatus())
            .isEqualTo(IdempotencyStatus.SUCCESS);

        assertThat(persistedSchedule.getStatus())
            .isEqualTo(ScheduledStatus.COMPLETED);

        assertThat(persistedSchedule.getNextRunAt())
            .isNull();

        assertThat(persistedSchedule.getLastIntendedLocal())
            .isEqualTo(
                LocalDateTime.ofInstant(
                    intendedRunAt,
                    schedule.getZoneId()
                )
            );
        
        assertThat(transactionRepository.findAll())
            .hasSize(2);
    }

    @Test
    void execute_whenInsufficientFunds_shouldRollbackMoneyAndScheduleThenMarkExecutionFailed() {
        sourceAccount.setBalance(new BigDecimal("10.00"));
        accountRepository.saveAndFlush(sourceAccount);

        UUID executionId =
            claimer.claim(schedule.getId(), intendedRunAt);

        BigDecimal originalSourceBalance = sourceAccount.getBalance();
        BigDecimal originalDestinationBalance = destinationAccount.getBalance();
        Instant originalNextRunAt = schedule.getNextRunAt();

        assertThatThrownBy(() -> executor.execute(executionId))
            .isInstanceOf(AppException.class);
        
        failureHandler.markFailed(executionId);
        entityManager.clear();

        Account reloadedSource = accountRepository.findById(sourceAccount.getId()).orElseThrow();
        Account reloadedDestination = accountRepository.findById(destinationAccount.getId()).orElseThrow();
        ScheduledTransaction reloadedSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        ScheduledTransactionExecution reloadedExecution = executionRepository.findById(executionId).orElseThrow();

        assertThat(reloadedSource.getBalance())
            .isEqualByComparingTo(originalSourceBalance);

        assertThat(reloadedDestination.getBalance())
            .isEqualByComparingTo(originalDestinationBalance);

        assertThat(reloadedSchedule.getNextRunAt())
            .isEqualTo(originalNextRunAt);

        assertThat(reloadedSchedule.getStatus())
            .isEqualTo(ScheduledStatus.ACTIVE);

        assertThat(reloadedExecution.getStatus())
            .isEqualTo(IdempotencyStatus.FAILED);

        assertThat(transactionRepository.count()).isZero();
    }

    @Test
    void claim_whenTwoWorkersCompete_shouldAllowOnlyOne()
            throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        UUID scheduleId = schedule.getId();

        Callable<Object> claimTask = () -> {
            ready.countDown();
            start.await();

            try {
                return claimer.claim(scheduleId, intendedRunAt);
            } catch (DataIntegrityViolationException e) {
                return e;
            }
        };

        try {
            Future<Object> first = pool.submit(claimTask);
            Future<Object> second = pool.submit(claimTask);

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();

            start.countDown();

            Object firstResult = first.get(10, TimeUnit.SECONDS);
            Object secondResult = second.get(10, TimeUnit.SECONDS);

            long successCount = Stream.of(firstResult, secondResult)
                    .filter(UUID.class::isInstance)
                    .count();

            long failureCount = Stream.of(firstResult, secondResult)
                    .filter(DataIntegrityViolationException.class::isInstance)
                    .count();

            assertThat(successCount).isEqualTo(1);
            assertThat(failureCount).isEqualTo(1);
            assertThat(executionRepository.count()).isEqualTo(1);

        } finally {
            start.countDown();
            pool.shutdownNow();
        }
    }
}
