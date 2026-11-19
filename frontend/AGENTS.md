# Frontend contributor guide

These instructions apply to everything under `frontend/`.

## Purpose and boundaries

This React application is a client for the Spring Boot API in the repository root. Treat Java controllers, DTOs, enums, security configuration, and exception handlers as the source of truth. Do not modify backend business logic from a frontend task. Never invent a route, field, enum member, role, pagination convention, or response envelope. Record missing capabilities in `docs/API_INTEGRATION_PLAN.md`.

## Architecture

- `src/app/`: application composition, providers, routing, guards, and the authenticated shell.
- `src/components/ui/`: shadcn/ui-compatible primitives only. Keep product-specific components out.
- `src/features/<feature>/`: vertical feature code, normally split into `api/`, `components/`, and `model/`.
- `src/lib/api/`: Axios transport, shared API types, and transport-level error handling.
- `src/pages/`: route-level composition. Pages should delegate feature behavior rather than contain API calls.
- `docs/API_INTEGRATION_PLAN.md`: verified mapping between controllers and planned UI.

Prefer direct feature imports over barrel files. Use the `@/` alias. Keep abstractions local until at least two real consumers need them.

## UI conventions

- Use Tailwind utilities and the semantic tokens in `src/styles.css`; do not scatter raw brand colors through components.
- Add reusable primitives through shadcn/ui conventions and keep them accessible: labels, keyboard focus, semantic elements, status text, and reduced-motion behavior are required.
- Design mobile-first. Verify both narrow and desktop layouts.
- Use sentence case. Actions state exactly what happens. Empty and error states tell the user what to do next.
- Use tabular numerals for balances and transaction amounts. Do not infer a currency when a DTO supplies one; the current account summary lacks currency and the temporary USD display must be revisited before production.

## API integration rules

- All calls go through `src/lib/api/client.ts`. Feature request functions live under their feature's `api/` folder.
- The default base URL is `/`. Development proxies `/api` and `/admin` to `VITE_DEV_PROXY_TARGET`; deployment must route those paths to Spring Boot on the same origin unless backend CORS is explicitly configured.
- Successful endpoints return their DTOs directly. `RestResponse` is the error shape, not a success wrapper used by current controllers.
- Send the access token as `Authorization: Bearer <token>`.
- Login and refresh return both `accessToken` and `refreshToken`. Refresh rotates and invalidates the prior refresh token. Update both atomically.
- Transfer requests require a fresh, client-generated `Idempotency-Key`. Reuse the same key only when retrying the exact same payload; generate a new key for a changed or newly confirmed transfer.
- Backend page numbers start at `1`, not `0`. Preserve the exact kebab-case transaction query names.
- Model Java `BigDecimal` values conservatively. JavaScript `number` can lose precision; use strings for user-entered monetary values and convert only for display when the API serialization has been confirmed.
- Parse API errors defensively. Validation field errors are currently placed in `data`; other failures commonly provide `message` and `statusCode`.
- TanStack Query owns server state. React Hook Form plus Zod owns form state and client-side validation. Backend validation remains authoritative.
- Query keys must be stable arrays scoped by feature and include every filter that changes the result.

## Security and privacy

- Never commit secrets or put credentials, JWTs, account data, or personal data in logs, analytics, URLs, fixtures, snapshots, or error reports.
- Tokens currently live only in module memory. Do not move them to `localStorage`, IndexedDB, or non-HttpOnly cookies. A reload requiring sign-in is an accepted limitation until the backend supports a secure cookie-based session/refresh design.
- Clear local session state immediately on sign-out or failed refresh. Attempt server logout without blocking local sign-out.
- Do not decode a JWT and treat its claims as authorization. Route visibility is convenience only; the backend is authoritative.
- Mask account numbers by default. Avoid caching sensitive responses longer than the user task requires.
- Do not render server-provided HTML. Do not place sensitive values in query strings.
- Keep `.env` files local. Only variables prefixed with `VITE_` are browser-exposed, so they must never contain secrets.

## Adding a feature

1. Verify the controller method, path, authorization rule, DTO fields, enum values, and error behavior.
2. Update `docs/API_INTEGRATION_PLAN.md` if the mapping or a known gap changes.
3. Add exact transport types and a small request function under the feature.
4. Add query/mutation hooks only when reuse or lifecycle behavior warrants them.
5. Build the accessible UI, including loading, empty, failure, and success states.
6. Invalidate the narrowest affected query keys after a mutation.
7. Run all verification commands.

## Verification

From `frontend/`:

```bash
npm install
npm run typecheck
npm run lint
npm run build
```

For interactive verification, run the backend on port `8080`, then `npm run dev`. Test login failure, login success, token refresh, logout, direct navigation to a protected route, mobile layout, keyboard navigation, and backend-unavailable states. Do not use production customer data for local verification.
