# Codebase Review Findings

Date: 2026-08-31

Project: `bank-spring-boot`

## Overall Verdict

This is a solid backend portfolio project, but it is not ready yet to be the main repository on a software engineering or backend resume.

Estimated current readiness: **62-66/100**.

Strengths:

- Spring Boot 3 + Java 21 backend with realistic banking domain.
- MySQL, Redis, Docker Compose, JWT, RBAC, idempotency, caching, and scheduled transaction concepts.
- Transfer core has a good technical story around deterministic lock ordering.
- README is much stronger than before and explains tradeoffs honestly.
- `./mvnw -q clean test` currently passes.

Main problem:

The project still needs stronger reviewer trust. A recruiter or engineer should be able to clone it, run it, see green CI, read clean docs, and trust that the banking business rules are enforced by tests.

## Critical Findings

### 1. Fresh Docker Startup May Fail

The Docker setup looks good structurally, but a fresh MySQL database may not work with the current production profile.

Relevant files:

- `.env.example`
- `docker-compose.yaml`
- `src/main/resources/application-prod.yml`

Problem:

- `.env.example` defaults to the production profile.
- `application-prod.yml` uses `spring.jpa.hibernate.ddl-auto: validate`.
- There are no Flyway migrations.
- A fresh MySQL container will not have the required schema.

Why this matters:

A reviewer may run `docker compose up` and hit a startup failure immediately.

Recommended fix:

- Add Flyway migrations, or
- Make Docker Compose use a development profile that can create schema, and document that clearly.

Best portfolio option:

Add Flyway. It is a strong backend signal.

### 2. Authentication Exceptions May Return 500

Relevant files:

- `src/main/java/com/example/bankspringboot/exceptions/GlobalExceptionHandler.java`
- `src/main/java/com/example/bankspringboot/service/CustomUserDetailsService.java`

Problem:

`CustomUserDetailsService` correctly blocks non-active customers, but `GlobalExceptionHandler` imports `javax.naming.AuthenticationException`, not Spring Security authentication exceptions.

This means failures such as disabled users or bad credentials may fall through to the generic runtime handler and return `500`.

Recommended fix:

- Handle `org.springframework.security.core.AuthenticationException`.
- Consider explicit handling for `BadCredentialsException`, `DisabledException`, and access denied cases.

### 3. Scheduled Transactions Do Not Match Business Rules

Relevant files:

- `BUSINESS_RULES.md`
- `src/main/java/com/example/bankspringboot/enums/ScheduledStatus.java`
- `src/main/java/com/example/bankspringboot/service/ScheduledTransactionService.java`
- `src/main/java/com/example/bankspringboot/service/ScheduledTransactionExecutor.java`
- `src/main/java/com/example/bankspringboot/controller/ScheduledTransactionController.java`

Problems:

- Business rules describe `PENDING -> PROCESSING -> COMPLETED/FAILED`.
- Code uses `ACTIVE`, `PAUSED`, `CANCELLED`, `FAILED`.
- New schedules are created as `ACTIVE`, not `PENDING`.
- Business rules say only transfers can be scheduled.
- Code still allows scheduled deposits in some paths.
- There is no complete cancel flow exposed through the controller.
- Failure status updates may be rolled back because they happen inside the same transaction that rethrows the failure.

Recommended fix:

- Decide the final scheduled transaction lifecycle.
- Update enum, service, executor, and API to match `BUSINESS_RULES.md`.
- Add tests for create, execute, fail, recur, and cancel.

## High-Priority Findings

### 4. Money and Channel Limits Are Missing

Relevant files:

- `BUSINESS_RULES.md`
- `src/main/java/com/example/bankspringboot/service/TransactionExecutor.java`
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`

Problems:

- ATM withdrawal limit should be `2,000`.
- ATM deposit limit should be `10,000`.
- Online withdrawal limit should be `20,000`.
- These limits are not enforced in the transaction executor.
- Customer max balance config appears much higher than the business rules.

Recommended fix:

- Move limits into `@ConfigurationProperties`.
- Enforce limits in service code.
- Add tests for each channel and transaction type.

### 5. Integration Testing Is Too Thin

Current verification:

- `./mvnw -q clean test` passes.
- Current test count is low.
- JaCoCo is around 23.6% line coverage and 11.5% branch coverage.

Relevant files:

- `src/test/java/com/example/bankspringboot/BaseIntegrationTest.java`
- `src/test/java/com/example/bankspringboot/repository/AccountRepositoryTest.java`
- `src/test/java/com/example/bankspringboot/service/TransactionExecutorTest.java`
- `pom.xml`

Problems:

- `BaseIntegrationTest` exists but no important integration test extends it yet.
- Repository tests still use H2 instead of Testcontainers MySQL.
- Locking, native SQL, report queries, idempotency, and auth flows need integration tests.

Recommended tests before showing the repo:

- Transfer concurrency test.
- IDOR/auth test.
- Idempotency replay and conflict test.
- Report date-boundary test on MySQL.
- Scheduled transaction failure test.
- Cache filter-key test.

### 6. Formatting Gate Fails

Relevant file:

- `pom.xml`

Problem:

`./mvnw -q spotless:check` fails across many files.

Recommended fix:

- Run `./mvnw spotless:apply`.
- Add Spotless execution to the Maven lifecycle or enforce it in CI.

## Medium-Priority Findings

### 7. Account Cascade Can Delete Financial History

Relevant file:

- `src/main/java/com/example/bankspringboot/domain/Account.java`

Problem:

`Account.transactions` uses `cascade = CascadeType.ALL`.

Why this matters:

Financial transactions should not be deleted just because an account entity is deleted.

Recommended fix:

- Remove `CascadeType.ALL` from account-to-transaction mapping.
- Prefer closing/deactivating accounts over deleting them.

### 8. Account Statistics Exclude Zero-Transaction Accounts

Relevant file:

- `src/main/java/com/example/bankspringboot/repository/AccountRepository.java`

Problem:

The account transaction count query uses an inner join, so accounts with no transactions are excluded.

Recommended fix:

- Use a `LEFT JOIN`.
- Add a repository test for an account with zero transactions.

### 9. README Still Has Drift

Relevant files:

- `README.md`
- `docs/`

Problems:

- Screenshot references exist, but `docs/` does not contain real screenshots yet.
- Some documentation claims are ahead of the implementation.
- Some old limitations may now be fixed in code and should be updated.

Recommended fix:

- Add real Swagger/API screenshots.
- Keep a clear `Known limitations` section.
- Do not claim production readiness.

## Recommended Order Before Applying

### Must Fix Before Putting Repo On CV

1. Make Docker Compose work from a fresh clone.
2. Add Flyway or clearly documented dev schema creation.
3. Fix authentication exception handling.
4. Align scheduled transactions with `BUSINESS_RULES.md`.
5. Enforce ATM/online/channel limits.
6. Run and enforce Spotless.
7. Add GitHub Actions CI.
8. Add 4-6 high-value integration tests.
9. Update README screenshots and claims.

### Best Tests To Add First

1. `TransferConcurrencyIT`
2. `CustomerAuthorizationIT`
3. `IdempotencyIT`
4. `FinancialReportBoundaryIT`
5. `ScheduledTransactionIT`
6. `TransactionHistoryCacheIT`

Do not chase 90% total coverage. Aim for meaningful coverage on risky service and transaction behavior.

## Resume Positioning

Good resume bullet:

> Built a Spring Boot banking API with JWT RBAC, MySQL, Redis caching, Docker Compose, OpenAPI, idempotency keys, and concurrency-safe money transfers using deterministic pessimistic lock ordering.

Another good bullet after tests are added:

> Added integration and concurrency tests for transfer correctness, authorization boundaries, idempotency replay, and MySQL report queries.

Avoid claiming:

- Production ready.
- Fully compliant banking system.
- Complete scheduled transaction implementation.
- Complete financial ledger or double-entry accounting.

Better wording:

- Production-oriented backend project.
- Banking-domain portfolio API.
- Concurrency-safe transfer workflow.
- Documented known limitations and roadmap.

## Job Application Plan

Since graduation is at the end of 2026:

### September

- Finish the must-fix repo work.
- Make README polished.
- Add CI badge.
- Add screenshots.
- Add high-value integration tests.

### October

- Start applying.
- Use this as the main backend project.
- Prepare interview explanations around locking, idempotency, caching, validation, and tradeoffs.

### November-December

- Keep applying.
- Add deeper tests and production-readiness improvements.
- Prepare short technical stories from this repo for behavioral and system-design interviews.

## Final Recommendation

This project is worth finishing and using for backend applications.

The highest-return improvements are not more features. They are:

- Fresh clone works.
- CI is green.
- Tests prove the risky money logic.
- README is honest.
- Business rules and code match.

Once those are done, this can be a strong graduate backend portfolio repo.
