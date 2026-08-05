# BUSINESS_RULES.md

# Banking System Business Rules

## Overview

This document defines the business rules used by the Bank Spring Boot project. These rules are intentionally designed to resemble a modern digital banking system while remaining suitable for a portfolio project and technical interviews.

---

# 1. Account Rules

An account must always have one of the following statuses.

## ACTIVE

The account is fully operational.

Allowed operations:

* Deposit
* Withdraw
* Transfer money
* Receive incoming transfers
* Schedule future transfers

---

## FROZEN

The account is temporarily restricted.

Possible reasons:

* Suspicious activity
* Customer request
* Fraud investigation

Allowed operations:

* Receive incoming deposits

Not allowed:

* Withdraw
* Transfer
* Create scheduled transfers

Scheduled transfers waiting to execute will fail.

---

## CLOSED

The account is permanently closed.

Not allowed:

* Deposit
* Withdraw
* Transfer
* Receive transfers
* Create scheduled transfers

---

# 2. Customer Types

## INDIVIDUAL

Maximum transaction amount:

```
50,000 AUD
```

---

## BUSINESS

Maximum transaction amount:

```
500,000 AUD
```

These limits apply to a single transaction.

---

# 3. Transaction Types

```java
public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER_OUT,
    TRANSFER_IN
}
```

Definitions:

| Type         | Description                           |
| ------------ | ------------------------------------- |
| DEPOSIT      | Money enters an account               |
| WITHDRAW     | Money leaves an account               |
| TRANSFER_OUT | Money sent from source account        |
| TRANSFER_IN  | Money received by destination account |

For every successful transfer:

* one `TRANSFER_OUT`
* one `TRANSFER_IN`

transaction should be recorded.

---

# 4. Transaction Channels

```java
public enum TransactionChannel {
    ONLINE,
    ATM,
    BRANCH,
    SCHEDULED
}
```

The transaction channel represents **how the transaction was initiated or executed**.

---

## ONLINE

Initiated through the banking application.

Requirements:

* authenticated customer

No branch or ATM information is required.

Allowed:

* Deposit
* Withdraw
* Transfer

---

## ATM

Initiated through an ATM.

Requirements:

* ATM identifier (or ATM location)

Allowed:

* Cash deposit
* Cash withdrawal

Not allowed:

* Transfer

Maximum withdrawal:

```
2,000 AUD
```

Maximum cash deposit:

```
10,000 AUD
```

---

## BRANCH

Initiated by a bank employee at a physical branch.

Requirements:

* Branch identifier

Allowed:

* Deposit
* Withdraw
* Transfer

No transaction amount limitation (business rules still apply).

---

## SCHEDULED

Executed automatically by the scheduler.

Characteristics:

* Cannot be manually executed by users.
* Always created as a pending scheduled instruction.
* Executed by the scheduled job.
* Internally performs a transfer.

Only transfer operations may use the scheduled channel.

---

# 5. Transaction Status

```java
public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
```

---

## PENDING

Waiting for execution.

Example:

* Scheduled transfer created.

---

## PROCESSING

Transaction has started but is not yet completed.

Example:

* Scheduler is currently executing the transfer.

---

## COMPLETED

Transaction finished successfully.

---

## FAILED

Execution was attempted but unsuccessful.

Examples:

* Insufficient balance
* Destination account closed
* Daily limit exceeded
* Account frozen

---

## CANCELLED

Execution never occurred.

Examples:

* Customer cancelled scheduled transfer.
* Administrator cancelled scheduled transfer.

---

# 6. Deposit Rules

A deposit is allowed only when:

* account exists
* account is ACTIVE
* amount > 0

Additional rules:

### ONLINE

Allowed.

No additional information required.

---

### ATM

Allowed.

Requires:

* ATM identifier

Maximum amount:

```
10,000 AUD
```

---

### BRANCH

Allowed.

Requires:

* Branch identifier

No channel-specific limit.

---

Cannot deposit into:

* CLOSED account

---

# 7. Withdraw Rules

Withdrawal is allowed only when:

* account exists
* account is ACTIVE
* amount > 0
* sufficient balance

---

### ONLINE

Allowed.

Maximum amount:

```
20,000 AUD
```

---

### ATM

Allowed.

Requires:

* ATM identifier

Maximum amount:

```
2,000 AUD
```

---

### BRANCH

Allowed.

Requires:

* Branch identifier

No channel-specific limit.

---

Withdraw is NOT allowed when:

* account is FROZEN
* account is CLOSED
* insufficient balance

---

# 8. Transfer Rules

Transfer is allowed only when:

* source account exists
* destination account exists
* source account is ACTIVE
* destination account is ACTIVE
* amount > 0
* source != destination
* sufficient balance
* transaction amount <= customer transaction limit

---

ONLINE

Allowed.

---

BRANCH

Allowed.

---

ATM

Not allowed.

---

SCHEDULED

Allowed.

Executed automatically by scheduler.

---

Each successful transfer creates:

* one TRANSFER_OUT
* one TRANSFER_IN

---

# 9. Scheduled Transaction Rules

Only transfers may be scheduled.

When a scheduled transfer is created:

Status:

```
PENDING
```

Execution time:

```
must be after current time
```

Supported frequencies:

* DAILY
* WEEKLY
* MONTHLY

Execution flow:

```
PENDING
    ↓
PROCESSING
    ↓
COMPLETED
```

If execution fails:

```
FAILED
```

If recurring:

* update nextRunAt
* return to PENDING

Customer may cancel before execution.

Cancelled schedule:

```
CANCELLED
```

---

# 10. Validation Rules

## TransferRequest

Validate:

* amount > 0
* source account != destination account
* scheduled execution time is in the future
* ATM channel is not allowed
* BRANCH requires branchId
* ONLINE must not include branchId

---

## DepositRequest

Validate:

* amount > 0

ATM:

* requires atmId

BRANCH:

* requires branchId

ONLINE:

* must not include branchId
* must not include atmId

---

## WithdrawRequest

Validate:

* amount > 0

ATM:

* requires atmId

BRANCH:

* requires branchId

ONLINE:

* must not include atmId
* must not include branchId

---

# 11. Authorization Rules

## Customer

Can:

* View own accounts
* View own transactions
* Deposit into own account
* Withdraw from own account
* Transfer from own account
* Create scheduled transfers
* Cancel own scheduled transfers

Cannot:

* Create customers
* Create accounts
* Freeze accounts
* View other customers

---

## Administrator

Can:

* Create customers
* Create accounts
* Freeze accounts
* Unfreeze accounts
* View all customers
* View all transactions
* Cancel any scheduled transfer

---

# 12. Error Handling

| Business Rule              | Exception                         | HTTP Status               |
| -------------------------- | --------------------------------- | ------------------------- |
| Account not found          | AccountNotFoundException          | 404 Not Found             |
| Customer not found         | CustomerNotFoundException         | 404 Not Found             |
| Insufficient balance       | InsufficientBalanceException      | 409 Conflict              |
| Account frozen             | AccountFrozenException            | 409 Conflict              |
| Account closed             | AccountClosedException            | 409 Conflict              |
| Transaction limit exceeded | TransactionLimitExceededException | 409 Conflict              |
| Invalid transfer           | InvalidTransferException          | 409 Conflict              |
| Validation failure         | MethodArgumentNotValidException   | 400 Bad Request           |
| Authentication required    | AuthenticationException           | 401 Unauthorized          |
| Access denied              | AccessDeniedException             | 403 Forbidden             |
| Unexpected server error    | Exception                         | 500 Internal Server Error |

---

# 13. Design Principles

This project follows the following principles:

* Keep controllers thin.
* Business rules belong in the service layer.
* Request consistency belongs in the validation layer.
* Use Bean Validation for request validation.
* Use custom class-level validators for conditional validation.
* Throw domain-specific exceptions for business rule violations.
* Use HTTP status codes that reflect the type of failure.
* Keep business rules deterministic and easy to test.
* Record every successful transfer as both an outgoing and incoming transaction.
* Scheduled transactions are instructions, while executed transfers are normal transactions with the `SCHEDULED` transaction channel.

---

# Future Improvements

Potential enhancements for future versions:

* Daily withdrawal limits
* Daily transfer limits
* International transfers (SWIFT)
* Multi-currency accounts
* Foreign exchange transactions
* Interest calculation
* Loan accounts
* Overdraft facilities
* Transaction fees
* ATM cash availability
* Branch operating hours
* Fraud detection
* Two-factor authentication for high-value transfers
* Transaction notifications
* Audit logging
* Account statements
