import { useQuery } from "@tanstack/react-query";
import { AlertCircle, ArrowUpRight } from "lucide-react";
import { accountKeys, getMyAccounts } from "../api/accounts";

const money = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" });

export function AccountList() {
  const accounts = useQuery({ queryKey: accountKeys.mine, queryFn: getMyAccounts });

  if (accounts.isPending) return <div className="h-44 animate-pulse rounded-lg bg-muted" aria-label="Loading accounts" />;
  if (accounts.isError) {
    return (
      <div className="flex gap-3 rounded-lg border border-destructive/25 p-4 text-sm text-destructive" role="alert">
        <AlertCircle className="size-5 shrink-0" />
        Accounts could not be loaded. Check that the banking service is running, then refresh.
      </div>
    );
  }
  if (accounts.data.length === 0) {
    return <p className="rounded-lg border border-dashed p-8 text-center text-sm text-muted-foreground">No accounts are linked to this customer yet.</p>;
  }

  return (
    <div className="divide-y divide-border border-y border-border">
      {accounts.data.map((account) => (
        <article key={account.accountId} className="grid grid-cols-[1fr_auto] items-center gap-4 py-5">
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-semibold">Account •••• {account.accountNumber.slice(-4)}</h3>
              <span className="rounded-full bg-accent px-2 py-0.5 text-xs text-accent-foreground">{account.status.toLowerCase()}</span>
            </div>
            <p className="mt-1 text-sm text-muted-foreground">Available balance</p>
          </div>
          <div className="flex items-center gap-4">
            <p className="number text-xl font-semibold">{money.format(account.balance)}</p>
            <ArrowUpRight className="size-4 text-muted-foreground" aria-hidden />
          </div>
        </article>
      ))}
    </div>
  );
}
