package com.example.bankspringboot.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bankspringboot.config.properties.TransactionProperties;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountType;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.events.TransactionCreatedEvent;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.TransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionExecutorTest {

  /**
   * Account ids are fixed and ordered rather than random, because {@link
   * TransactionExecutor#executeTransfer} locks the two accounts in ascending id order. With random
   * ids the lock order — and therefore which repository stub is consumed first — would change from
   * run to run, making these tests flaky under Mockito's strict stubbing.
   *
   * <p>LOW &lt; MID &lt; HIGH &lt; UNKNOWN.
   */
  static final UUID LOW_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  static final UUID MID_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
  static final UUID HIGH_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
  static final UUID UNKNOWN_ID = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

  /** The fee the executor applies to transfers and withdrawals. */
  static final BigDecimal FEE_RATE = new BigDecimal("0.01");

  @Mock TransactionRepository transactionRepository;

  @Mock AccountRepository accountRepository;

  @Mock ApplicationEventPublisher eventPublisher;

  TransactionExecutor transactionExecutor;

  Account source;
  Account destination;
  Account closedAccount;

  @BeforeEach
  void setUp() {
    TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    source = account(LOW_ID, customer("vuongkhang2005@gmail.com"), "1234567890", AccountStatus.ACTIVE);
    destination =
        account(MID_ID, customer("vuongkhang2004@gmail.com"), "0123456789", AccountStatus.ACTIVE);
    closedAccount =
        account(HIGH_ID, customer("vuongkhang2003@gmail.com"), "5123456789", AccountStatus.CLOSED);

    // A real value object rather than a mock: it carries no behaviour, and using the real thing
    // keeps FEE_RATE the single source of truth for what the tests expect.
    TransactionProperties transactionProperties = new TransactionProperties(FEE_RATE);

    transactionExecutor =
        new TransactionExecutor(
            transactionRepository,
            accountRepository,
            transactionProperties,
            transactionMapper,
            eventPublisher);
  }

  // ---------------------------------------------------------------------------------------------
  // Fixtures
  // ---------------------------------------------------------------------------------------------

  private DepositRequest depositRequest() {
    return DepositRequest.builder()
        .amount(BigDecimal.valueOf(1000))
        .description("chuyen tien cho Khang")
        .currency("USD")
        .address(address())
        .channel(TransactionChannel.ATM)
        .build();
  }

  private TransferRequest transferRequest(UUID destinationAccountId, BigDecimal amount) {
    return TransferRequest.builder()
        .destinationAccountId(destinationAccountId)
        .amount(amount)
        .description("chuyen tien cho Khang")
        .currency("USD")
        .address(address())
        .channel(TransactionChannel.ONLINE)
        .build();
  }

  private Address address() {
    return Address.builder()
        .street("Van Coi Street")
        .city("Ho Chi Minh City")
        .postalCode("70000")
        .country("Viet Nam")
        .build();
  }

  private Customer customer(String email) {
    return Customer.builder()
        .id(UUID.randomUUID())
        .email(email)
        .password("password")
        .firstName("Khang")
        .lastName("Vuong")
        .phoneNumber("0123456789")
        .birthDate(LocalDate.of(2005, 1, 1))
        .address(address())
        .build();
  }

  private Account account(UUID id, Customer customer, String accountNumber, AccountStatus status) {
    return Account.builder()
        .id(id)
        .accountNumber(accountNumber)
        .balance(BigDecimal.ZERO)
        .transactionLimit(BigDecimal.valueOf(100000))
        .createdAt(Instant.now())
        .customer(customer)
        .status(status)
        .accountType(AccountType.SAVINGS)
        .build();
  }

  /** Makes {@code transactionRepository.save(..)} echo its argument back, as JPA would. */
  private void stubTransactionSaveEchoesArgument() {
    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private void stubLock(Account account) {
    when(accountRepository.findByIdWithPessimisticLock(account.getId()))
        .thenReturn(Optional.of(account));
  }

  /** Returns the transactions passed to {@code transactionRepository.save(..)}, in call order. */
  private List<Transaction> capturedTransactions(int expectedCount) {
    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository, times(expectedCount)).save(captor.capture());
    return captor.getAllValues();
  }

  // ---------------------------------------------------------------------------------------------
  // Deposit
  // ---------------------------------------------------------------------------------------------

  @Test
  void deposit_shouldIncreaseBalance() {
    DepositRequest request = depositRequest();
    source.setBalance(BigDecimal.valueOf(500));

    stubLock(source);
    stubTransactionSaveEchoesArgument();

    TransactionResponse response = transactionExecutor.executeDeposit(source.getId(), request);

    assertThat(source.getBalance()).isEqualByComparingTo("1500");
    assertThat(response.getAmount()).isEqualByComparingTo("1000");
    assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

    verify(accountRepository).findByIdWithPessimisticLock(source.getId());
    verify(transactionRepository).save(any(Transaction.class));
    verify(eventPublisher).publishEvent(any(TransactionCreatedEvent.class));
  }

  @Test
  void deposit_shouldRejectInactiveAccount() {
    DepositRequest request = depositRequest();
    closedAccount.setBalance(BigDecimal.valueOf(500));

    stubLock(closedAccount);

    assertThatThrownBy(() -> transactionExecutor.executeDeposit(closedAccount.getId(), request))
        .isInstanceOf(AppException.class)
        .extracting(ex -> ((AppException) ex).getErrorCode())
        .isEqualTo(ErrorCode.TRANSFER_ERROR);

    assertThat(closedAccount.getBalance()).isEqualByComparingTo("500");

    verify(transactionRepository, never()).save(any(Transaction.class));
    verifyNoInteractions(eventPublisher);
  }

  // ---------------------------------------------------------------------------------------------
  // Transfer — happy path
  // ---------------------------------------------------------------------------------------------

  @Test
  void transfer_shouldMoveMoneyAndChargeFeeToSourceOnly() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(500));
    source.setBalance(BigDecimal.valueOf(1000));
    destination.setBalance(BigDecimal.valueOf(500));

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    TransactionResponse response =
        transactionExecutor.executeTransfer(source.getId(), request);

    // Source pays amount + 1% fee; destination receives the amount only.
    assertThat(source.getBalance()).isEqualByComparingTo("495");
    assertThat(destination.getBalance()).isEqualByComparingTo("1000");

    // The caller is shown the outgoing leg.
    assertThat(response.getTransactionType()).isEqualTo(TransactionType.TRANSFER_OUT);
    assertThat(response.getAmount()).isEqualByComparingTo("500");
    assertThat(response.getFee()).isEqualByComparingTo("5");
    assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    assertThat(response.getCustomerId()).isEqualTo(source.getCustomer().getId());
  }

  @Test
  void transfer_shouldRecordOneOutgoingAndOneIncomingLeg() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(500));
    source.setBalance(BigDecimal.valueOf(1000));
    destination.setBalance(BigDecimal.ZERO);

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    transactionExecutor.executeTransfer(source.getId(), request);

    List<Transaction> saved = capturedTransactions(2);
    Transaction outgoing = saved.get(0);
    Transaction incoming = saved.get(1);

    assertThat(outgoing.getType()).isEqualTo(TransactionType.TRANSFER_OUT);
    assertThat(outgoing.getAccount()).isSameAs(source);
    assertThat(outgoing.getCustomer()).isSameAs(source.getCustomer());
    assertThat(outgoing.getAmount()).isEqualByComparingTo("500");
    assertThat(outgoing.getFee()).isEqualByComparingTo("5");
    assertThat(outgoing.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

    assertThat(incoming.getType()).isEqualTo(TransactionType.TRANSFER_IN);
    assertThat(incoming.getAccount()).isSameAs(destination);
    assertThat(incoming.getCustomer()).isSameAs(destination.getCustomer());
    assertThat(incoming.getAmount()).isEqualByComparingTo("500");
    assertThat(incoming.getFee()).isEqualByComparingTo("0");
    assertThat(incoming.getStatus()).isEqualTo(TransactionStatus.COMPLETED);

    // One event per leg, so downstream fraud checks see both sides.
    verify(eventPublisher, times(2)).publishEvent(any(TransactionCreatedEvent.class));
  }

  // ---------------------------------------------------------------------------------------------
  // Transfer — lock ordering (deadlock avoidance)
  // ---------------------------------------------------------------------------------------------

  @Test
  void transfer_shouldLockLowerIdFirst_whenSendingFromLowerToHigher() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(100));
    source.setBalance(BigDecimal.valueOf(1000));

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    transactionExecutor.executeTransfer(source.getId(), request);

    InOrder inOrder = inOrder(accountRepository);
    inOrder.verify(accountRepository).findByIdWithPessimisticLock(LOW_ID);
    inOrder.verify(accountRepository).findByIdWithPessimisticLock(MID_ID);
  }

  /**
   * The important half of the pair: even when the money flows from the higher id to the lower one,
   * the lower id is still locked first. Two opposing transfers therefore acquire locks in the same
   * order and cannot deadlock against each other.
   */
  @Test
  void transfer_shouldLockLowerIdFirst_whenSendingFromHigherToLower() {
    TransferRequest request = transferRequest(source.getId(), BigDecimal.valueOf(100));
    destination.setBalance(BigDecimal.valueOf(1000));

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    transactionExecutor.executeTransfer(destination.getId(), request);

    InOrder inOrder = inOrder(accountRepository);
    inOrder.verify(accountRepository).findByIdWithPessimisticLock(LOW_ID);
    inOrder.verify(accountRepository).findByIdWithPessimisticLock(MID_ID);
  }

  // ---------------------------------------------------------------------------------------------
  // Transfer — rejections
  // ---------------------------------------------------------------------------------------------

  @Test
  void transfer_shouldRejectTransferToSameAccount() {
    TransferRequest request = transferRequest(source.getId(), BigDecimal.valueOf(100));

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .extracting(ex -> ((AppException) ex).getErrorCode())
        .isEqualTo(ErrorCode.TRANSFER_ERROR);

    // Rejected before any row is locked.
    verifyNoInteractions(accountRepository, transactionRepository, eventPublisher);
  }

  @Test
  void transfer_shouldRejectInsufficientFunds() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(500));
    source.setBalance(BigDecimal.valueOf(100));
    destination.setBalance(BigDecimal.valueOf(50));

    stubLock(source);
    stubLock(destination);

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .extracting(ex -> ((AppException) ex).getErrorCode())
        .isEqualTo(ErrorCode.TRANSFER_ERROR);

    // Neither side is mutated — no partially applied transfer.
    assertThat(source.getBalance()).isEqualByComparingTo("100");
    assertThat(destination.getBalance()).isEqualByComparingTo("50");

    verify(transactionRepository, never()).save(any(Transaction.class));
    verifyNoInteractions(eventPublisher);
  }

  /**
   * The fee is charged on top of the amount, so a balance exactly equal to the amount is not enough.
   * Pins the boundary so a future change to fee handling cannot silently allow an overdraft.
   */
  @Test
  void transfer_shouldRequireBalanceToCoverAmountPlusFee() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(500));
    source.setBalance(BigDecimal.valueOf(500));

    stubLock(source);
    stubLock(destination);

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .hasMessageContaining("Insufficient funds");

    assertThat(source.getBalance()).isEqualByComparingTo("500");
  }

  @Test
  void transfer_shouldRejectAmountAboveTransactionLimit() {
    TransferRequest request = transferRequest(destination.getId(), BigDecimal.valueOf(500));
    source.setBalance(BigDecimal.valueOf(10000));
    source.setTransactionLimit(BigDecimal.valueOf(100));

    stubLock(source);
    stubLock(destination);

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .hasMessageContaining("TRANSACTION_LIMIT");

    assertThat(source.getBalance()).isEqualByComparingTo("10000");
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void transfer_shouldRejectInactiveSourceAccount() {
    TransferRequest request = transferRequest(source.getId(), BigDecimal.valueOf(100));
    closedAccount.setBalance(BigDecimal.valueOf(1000));

    stubLock(closedAccount);
    stubLock(source);

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(closedAccount.getId(), request))
        .isInstanceOf(AppException.class)
        .hasMessageContaining("not active");

    assertThat(closedAccount.getBalance()).isEqualByComparingTo("1000");
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void transfer_shouldRejectInactiveDestinationAccount() {
    TransferRequest request = transferRequest(closedAccount.getId(), BigDecimal.valueOf(100));
    source.setBalance(BigDecimal.valueOf(1000));

    stubLock(source);
    stubLock(closedAccount);

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .hasMessageContaining("not active");

    assertThat(source.getBalance()).isEqualByComparingTo("1000");
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void transfer_shouldRejectUnknownDestinationAccount() {
    TransferRequest request = transferRequest(UNKNOWN_ID, BigDecimal.valueOf(100));
    source.setBalance(BigDecimal.valueOf(1000));

    stubLock(source);
    when(accountRepository.findByIdWithPessimisticLock(UNKNOWN_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transactionExecutor.executeTransfer(source.getId(), request))
        .isInstanceOf(AppException.class)
        .extracting(ex -> ((AppException) ex).getErrorCode())
        .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

    assertThat(source.getBalance()).isEqualByComparingTo("1000");
    verifyNoInteractions(eventPublisher);
  }

  // ---------------------------------------------------------------------------------------------
  // Transfer — money arithmetic
  // ---------------------------------------------------------------------------------------------

  @Test
  void transfer_shouldChargeExactlyOnePercentOfTheAmount() {
    BigDecimal amount = new BigDecimal("2500.00");
    TransferRequest request = transferRequest(destination.getId(), amount);
    source.setBalance(new BigDecimal("5000.00"));

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    TransactionResponse response = transactionExecutor.executeTransfer(source.getId(), request);

    assertThat(response.getFee()).isEqualByComparingTo(FEE_RATE.multiply(amount));
    assertThat(response.getFee()).isEqualByComparingTo("25.00");
    assertThat(source.getBalance()).isEqualByComparingTo("2475.00");
  }

  /**
   * Regression test for a fee-rounding defect.
   *
   * <p>The fee used to be computed as {@code 0.01 * amount} with no scale or rounding mode. For an
   * amount of 100.55 that gives a fee of 1.0055 and reduces the balance by 101.5555. Because {@code
   * transactions.fee} and {@code accounts.balance} are {@code numeric(38,2)}, the persisted fee
   * rounded to 1.01 while the balance had been reduced using 1.0055 — the ledger and the balance
   * disagreed by fractions of a cent on every transfer, and the error accumulated.
   *
   * <p>Fixed by rounding at the point of calculation: {@code
   * feePercentage.multiply(amount).setScale(2, RoundingMode.HALF_UP)}.
   *
   * <p>Note the withdrawal path in {@code executeWithdrawal} does <em>not</em> yet round its fee and
   * still has this defect.
   */
  @Test
  void transfer_shouldRoundFeeToTwoDecimalPlaces() {
    TransferRequest request = transferRequest(destination.getId(), new BigDecimal("100.55"));
    source.setBalance(new BigDecimal("1000.00"));

    stubLock(source);
    stubLock(destination);
    stubTransactionSaveEchoesArgument();

    TransactionResponse response = transactionExecutor.executeTransfer(source.getId(), request);

    assertThat(response.getFee().scale()).isLessThanOrEqualTo(2);
    assertThat(response.getFee()).isEqualByComparingTo("1.01");
    assertThat(source.getBalance()).isEqualByComparingTo("898.44");
  }
}
