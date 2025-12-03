package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.transaction.Address;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.exceptions.BusinessException;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {
    final private TransactionRepository transactionRepository;
    final private AccountRepository accountRepository;
    final private BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);
    ModelMapper modelMapper;

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
                .orElseThrow(() -> new IdInvalidException("Account with id " + accountId + " not found"));

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
        transaction.setChannel(depositRequest.getDepositChannel());


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
            throw new IllegalArgumentException("Deposit amount must be positive");
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
        transaction.setChannel(req.getWithdrawalChannel());
        transaction.setAddress(req.getAddress());
        transaction.setDescription(req.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);

        // Save transaction after updating balance
        transactionRepository.save(transaction);
        return new TransactionResponse(transaction.getId(), transaction.getAccount().getId(),
                transaction.getAccount().getCustomer().getId(), transaction.getAmount(), transaction.getStatus(),
                transaction.getCreatedAt(), transaction.getDescription(), transaction.getFee(),
                transaction.getAddress(), transaction.getType());
    }
}
