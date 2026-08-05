package com.example.bankspringboot.service;

import com.example.bankspringboot.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountNumberGenerator {
    AccountRepository accountRepository;
    SecureRandom random = new SecureRandom();

    private String generateAccountNumber() {
        int LENGTH = 10; // length of the account number
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(random.nextInt(10)); // append a digit 0-9
        }
        return sb.toString();
    }

    public String generateUniqueAccountNumber() {
        String number;
        do {
            number = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
