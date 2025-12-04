package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.exceptions.BusinessException;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    final private TransactionRepository transactionRepository;
    final private AccountRepository accountRepository;
    final private BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);
    ModelMapper modelMapper;
    final private BigDecimal TRANSACTION_LIMIT = BigDecimal.valueOf(2000);

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository,
                              AccountService accountService, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public TransactionResponse deposit(DepositRequest depositRequest) {
        Long accountId = depositRequest.getAccountId();
        String description = depositRequest.getDescription();
        BigDecimal amount = depositRequest.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new BusinessException("Account with id " + accountId + " not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }

        // Update balance
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        // Build transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAccount(account);
        transaction.setCustomer(account.getCustomer());
        transaction.setChannel(depositRequest.getChannel());
        transaction.setCurrency(depositRequest.getCurrency());
        transaction.setAddress(depositRequest.getAddress());

        // Save transaction after updating balance
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
        return new TransactionResponse(transaction.getId(), transaction.getAccount().getId(),
                transaction.getAccount().getCustomer().getId(), transaction.getAmount(), transaction.getStatus(),
                transaction.getCreatedAt(), transaction.getDescription(), transaction.getFee(),
                transaction.getAddress(), transaction.getType());
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest req) {
        Long accountId = req.getAccountId();
        BigDecimal amount = req.getAmount();
        Account account = accountRepository.findByIdWithLock(accountId).orElseThrow(
                () -> new IdInvalidException("Account with id " + accountId + " not found"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Deposit amount must be positive");
        }

        if (amount.compareTo(TRANSACTION_LIMIT) > 0) {
            throw new BusinessException("Withdrawal amount must be less than or equal to TRANSACTION_LIMIT");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }

        BigDecimal fee = FEE_PERCENTAGE.multiply(amount);

        BigDecimal totalDeduction = amount.add(fee);
        if (account.getBalance().compareTo(totalDeduction) <= 0) {
            throw new BusinessException("Insufficient funds");
        }

        // Update balance
        account.setBalance(account.getBalance().subtract(totalDeduction));

        // Build transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAccount(account);
        transaction.setCustomer(account.getCustomer());
        transaction.setChannel(req.getChannel());
        transaction.setAddress(req.getAddress());
        transaction.setDescription(req.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCurrency(req.getCurrency());

        // Save transaction after updating balance
        transactionRepository.save(transaction);
        return new TransactionResponse(transaction.getId(), transaction.getAccount().getId(),
                transaction.getAccount().getCustomer().getId(), transaction.getAmount(), transaction.getStatus(),
                transaction.getCreatedAt(), transaction.getDescription(), transaction.getFee(),
                transaction.getAddress(), transaction.getType());
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest req) {
        Long fromId = req.getSourceAccountId();
        Long toId = req.getDestinationAccountId();
        BigDecimal amount = req.getAmount();
        Account fromAccount = accountRepository.findByIdWithLock(fromId).orElseThrow(
                () -> new IdInvalidException("Account with id " + fromId + " not found"));
        Account toAccount = accountRepository.findByIdWithLock(toId).orElseThrow(
                () -> new IdInvalidException("Account with id " + toId + " not found"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Deposit amount must be positive");
        }

        if (amount.compareTo(TRANSACTION_LIMIT) > 0) {
            throw new BusinessException("Transfer amount must be less than or equal to TRANSACTION_LIMIT");
        }

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }

        BigDecimal fee = FEE_PERCENTAGE.multiply(amount);

        BigDecimal totalDeduction = amount.add(fee);
        if (fromAccount.getBalance().compareTo(totalDeduction) <= 0) {
            throw new BusinessException("Insufficient funds");
        }

        // Update balance for source customer
        fromAccount.setBalance(fromAccount.getBalance().subtract(totalDeduction));

        // Update balance for destination customer
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Transaction for source
        Transaction txSource = new Transaction();
        txSource.setAmount(amount);
        txSource.setFee(fee);
        txSource.setType(TransactionType.TRANSFER_OUT);
        txSource.setAccount(fromAccount);
        txSource.setCustomer(fromAccount.getCustomer());
        txSource.setChannel(req.getChannel());
        txSource.setAddress(req.getAddress());
        txSource.setDescription(req.getDescription());
        txSource.setStatus(TransactionStatus.COMPLETED);
        txSource.setCurrency(req.getCurrency());

        // Transaction for destination
        Transaction txDest = new Transaction();
        txDest.setAmount(amount);
        txDest.setFee(BigDecimal.ZERO);
        txDest.setType(TransactionType.TRANSFER_IN);
        txDest.setAccount(toAccount);
        txDest.setCustomer(toAccount.getCustomer());
        txDest.setChannel(req.getChannel());
        txDest.setAddress(req.getAddress());
        txDest.setDescription(req.getDescription());
        txDest.setStatus(TransactionStatus.COMPLETED);
        txDest.setCurrency(req.getCurrency());

        // Save both
        transactionRepository.save(txSource);
        transactionRepository.save(txDest);

        return new TransactionResponse(
                txSource.getId(),
                txSource.getAccount().getId(),
                txSource.getCustomer().getId(),
                txSource.getAmount(),
                txSource.getStatus(),
                txSource.getCreatedAt(),
                txSource.getDescription(),
                txSource.getFee(),
                txSource.getAddress(),
                txSource.getType()
        );
    }
}
