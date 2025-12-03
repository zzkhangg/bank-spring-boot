package com.example.bankspringboot.dto.transaction;

import java.math.BigDecimal;

import com.example.bankspringboot.domain.transaction.Address;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * Data Transfer Object for a client-initiated Withdrawal transaction.
 * Represents funds moving OUT OF an account.
 */
@Getter
public class WithdrawalRequest {

    /**
     * The ID of the account from which the funds will be deducted.
     */
    @NotBlank(message = "Account ID is mandatory.")
    private Long accountId;

    /**
     * The amount of money to withdraw. Must be positive and non-zero.
     */
    @NotNull(message = "Amount is mandatory.")
    @DecimalMin(value = "0.01", inclusive = true, message = "The withdrawal amount must be positive.")
    @Digits(integer = 12, fraction = 2, message = "Amount can have at most 12 integer digits and 2 fraction digits.")
    private BigDecimal amount;

    /**
     * The channel through which the withdrawal is being performed (e.g., "ATM", "Online").
     */
    @NotBlank(message = "Channel is mandatory.")
    @NotNull
    private TransactionChannel channel;

    /**
     * An optional note or reference from the client.
     */
    @Size(max = 255, message = "Reference note cannot exceed 255 characters.")
    private String description;

    /**
     * The currency code (e.g., "USD", "EUR").
     */
    @NotBlank(message = "Currency is mandatory.")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g., USD).")
    private String currency;

    /**
     * Address
     */
    @Valid
    private Address address;
}
