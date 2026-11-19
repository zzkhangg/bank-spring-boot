# API integration plan

Verified against the controllers and DTOs under `src/main/java/com/example/bankspringboot` on 2026-11-19. Paths are relative to the frontend's configured API origin.

## Transport contract

- Authentication: bearer access JWT; refresh JWT is sent in a JSON body. Login and refresh return `{ accessToken, refreshToken }`.
- Public POST routes: `/api/customers`, `/api/auth/**`. All other routes require authentication. `/admin/customers/**` additionally requires `ROLE_ADMIN` through method security.
- Successful controller methods return DTO JSON directly, except `204` endpoints and financial-report file downloads.
- Errors use `{ message?, data?, statusCode, errors? }`. Validation field messages are stored in `data` by the current exception handler.
- Dev origin: Vite proxies `/api` and `/admin` to `http://localhost:8080` by default.

## Endpoint-to-feature mapping

| Feature | Method and path | Request / parameters | Response | Frontend status |
|---|---|---|---|---|
| Customer sign-in | `POST /api/auth/customer/login` | `{ email, password }` | auth tokens | Implemented |
| Admin sign-in | `POST /api/auth/admin/login` | `{ email, password }` | auth tokens | Later: separate admin surface |
| Refresh session | `POST /api/auth/refresh` | `{ refreshToken }` | rotated auth tokens | Implemented in Axios interceptor |
| Sign out | `POST /api/auth/logout` | `{ refreshToken }` | empty | Implemented |
| Registration | `POST /api/customers` | `CreateCustomerRequest` | `CustomerResponse` | Phase 2 |
| Profile | `GET /api/customers/me` | — | `CustomerResponse` | Phase 2 |
| Edit profile | `PATCH /api/customers/me` | `UpdateCustomerRequest` | `CustomerResponse` | Phase 2 |
| Change password | `PUT /api/customers/me/password` | `{ oldPassword, newPassword }` | `CustomerResponse` | Phase 2 |
| My accounts | `GET /api/accounts/myAccounts` | — | `AccountSummaryResponse[]` | Implemented |
| Account detail | `GET /api/accounts/{id}` | account UUID | `AccountResponse` | Phase 2 |
| List accounts | `GET /api/accounts?status=ACTIVE` | optional `AccountStatus` | `AccountResponse[]` | Admin phase; authorization is authenticated-only in config |
| Create account | `POST /api/accounts` | `{ email, accountType, initialDeposit, transactionLimit? }` | `AccountResponse` | Admin/operations phase; backend has no explicit method role guard |
| Delete account | `DELETE /api/accounts/{id}` | account UUID | `204` | Admin/operations phase |
| Update account status | `PUT /api/accounts/{id}/status` | `{ status, reason }` | `AccountResponse` | Admin/operations phase |
| Status history | `GET /api/accounts/{id}/status/history` | account UUID | `AccountStatusHistoryResponse[]` | Admin/operations phase |
| Transaction history | `GET /api/accounts/{accountId}/transactions` | `page` (starts at 1), `min-price`, `max-price`, `transaction-type`, `created-before`, `created-after`, `sort` | `PageResponse<TransactionResponse>` | Phase 3 |
| Transaction detail | `GET /api/accounts/{accountId}/transactions/{transactionId}` | two UUIDs | `TransactionResponse` | Phase 3 |
| Deposit | `POST /api/accounts/{accountId}/deposit` | `DepositRequest` | `TransactionResponse` | Phase 4 |
| Withdraw | `POST /api/accounts/{accountId}/withdraw` | `WithdrawalRequest` | `TransactionResponse` | Phase 4 |
| Transfer | `POST /api/accounts/{accountId}/transfer` | `TransferRequest` plus `Idempotency-Key` header | `TransactionResponse` | Phase 4, requires confirmation and retry semantics |
| Schedule transaction | `POST /api/accounts/{accountId}/scheduled-transactions` | `ScheduledTransactionRequest` | controller declares `204` despite response DTO | Blocked pending contract clarification |
| Account/transaction statistics | `GET /api/statistics/account-transactions-by-balance` | — | `AccountStatisticsDto[]` | Admin analytics phase |
| Customer city statistics | `GET /api/statistics/customers-by-city` | — | `AddressStatisticsDto[]` | Admin analytics phase |
| Weekly report | `GET /api/transaction-report/weekly` | — | `TransactionReportResponse[]` | Reporting phase |
| Quarterly report | `GET /api/transaction-report/quarterly` | — | `TransactionReportResponse[]` | Reporting phase |
| Yearly report | `GET /api/transaction-report/yearly` | — | `TransactionReportResponse[]` | Reporting phase |
| Financial export | `GET /api/reports/financial` | required `groupBy`; `format=EXCEL|PDF`; optional `from`, `to`, `year`, `month` | file bytes | Reporting phase |
| Admin customers | `GET /admin/customers` | — | `CustomerResponse[]` | Admin phase |
| Admin customer detail | `GET /admin/customers/{id}` | customer UUID | `CustomerResponse` | Admin phase |
| Admin create customer | `POST /admin/customers` | `CreateCustomerRequest` | `CustomerResponse` | Admin phase |
| Admin edit customer | `PATCH /admin/customers/{id}` | `UpdateCustomerRequest` | `CustomerResponse` | Admin phase |
| Admin customer type | `PATCH /admin/customers/{id}/customer-type` | `{ customerType }` | `CustomerResponse` | Admin phase |
| Admin delete customer | `DELETE /admin/customers/{id}` | customer UUID | `204` | Admin phase |

## Confirmed enums and notable DTO rules

- `AccountType`: `DEBIT`, `CREDIT`, `SAVINGS`; `AccountStatus`: `ACTIVE`, `FROZEN`, `CLOSED`.
- `TransactionType`: `DEPOSIT`, `WITHDRAW`, `TRANSFER_OUT`, `TRANSFER_IN`; `TransactionChannel`: `ATM`, `ONLINE`, `SCHEDULED`, `BRANCH`.
- Transaction amounts are at least `0.01`, at most 12 integer and 2 fraction digits. Currency is exactly three characters for immediate transactions.
- `address` contains `street`, `city`, `postalCode`, `country` and is required by validation when channel is `BRANCH`.
- Schedule types are `ONCE`, `DAILY`, `WEEKLY`, `MONTHLY`; schedule requests also require `zoneId` and `startAtLocal`.
- `CustomerResponse` has no birth date. `UpdateCustomerRequest` uses `phone`, while create/response use `phoneNumber`; clients must preserve this backend asymmetry.

## Missing or ambiguous backend capabilities

These are documented, not simulated:

- No CORS configuration is present. Same-origin proxying is required unless backend configuration is added.
- No cookie-based refresh flow exists. The frontend keeps tokens in memory, so a reload signs the user out. Durable secure sessions require an HttpOnly/Secure/SameSite cookie contract from the backend.
- No current-session or introspection endpoint exposes identity/authorities independently of `GET /api/customers/me`. Admin UI role discovery is therefore not reliable without decoding claims, which must not be treated as authorization.
- `AccountSummaryResponse` has no currency. The foundation displays USD only as a temporary presentation assumption; production multi-currency balances need an explicit backend field or product-wide currency rule.
- There are no list, pause, resume, edit, cancel, or detail endpoints for scheduled transactions.
- The schedule-create controller returns a DTO but is annotated `204 NO_CONTENT`; the browser cannot rely on the response body until this is resolved.
- There are no password-reset, forgot-password, email-verification, MFA, or account-number lookup endpoints.
- Immediate deposit and withdrawal have no idempotency header contract; only transfer does.
- Account administration endpoints lack explicit controller role annotations, unlike `/admin/customers`. Confirm intended authorization before exposing them.
- Transaction `sort` syntax is not constrained by the controller contract. Confirm supported values before building controls.
- Pagination response includes `size`, but the transaction endpoint exposes no page-size request parameter.

## Recommended implementation order

1. Harden the implemented session/login/account overview and confirm production reverse-proxy behavior.
2. Add profile and account detail, then replace the temporary account currency assumption.
3. Add paginated transaction history/detail with exact filters and safe amount formatting.
4. Add transfer as the first money movement flow: confirmation screen, fresh idempotency key, exact-payload retry, and narrow query invalidation.
5. Add deposit/withdraw only after product authorization and idempotency expectations are confirmed.
6. Resolve scheduled-transaction response and management gaps before implementing scheduling.
7. Build a separately guarded admin surface after role discovery and account endpoint authorization are explicit.
8. Add reporting/export last; it is independent of the core customer journey.
