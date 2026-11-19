# Banking frontend

React, TypeScript, Vite, Tailwind CSS, shadcn/ui-compatible primitives, React Router, TanStack Query, Axios, React Hook Form, and Zod.

```bash
cp .env.example .env
npm install
npm run dev
```

The development server proxies `/api` and `/admin` to the Spring Boot application at `http://localhost:8080`. Override `VITE_DEV_PROXY_TARGET` when the backend uses another origin.

See [AGENTS.md](./AGENTS.md) for contributor rules and [the API integration plan](./docs/API_INTEGRATION_PLAN.md) for the verified backend mapping and known gaps.
