import { ArrowDownLeft, ArrowRightLeft, ArrowUpRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { AccountList } from "@/features/accounts/components/account-list";

export function DashboardPage() {
  const today = new Intl.DateTimeFormat("en-US", {
    weekday: "long",
    day: "numeric",
    month: "long",
  }).format(new Date());

  return (
    <main className="mx-auto max-w-7xl px-5 py-8 lg:px-8 lg:py-12">
      <div className="grid gap-10 lg:grid-cols-[220px_minmax(0,1fr)]">
        <aside className="ledger-rail border-l-2 border-primary pl-6">
          <p className="text-sm text-muted-foreground">{today}</p>
          <h1 className="mt-3 text-3xl font-semibold tracking-[-0.03em]">Good banking starts with a clear view.</h1>
          <p className="mt-5 text-sm leading-6 text-muted-foreground">Your accounts and key actions, in one place.</p>
        </aside>
        <div className="min-w-0">
          <section aria-labelledby="accounts-heading">
            <div className="mb-5 flex items-end justify-between gap-4">
              <div><h2 id="accounts-heading" className="text-2xl font-semibold tracking-[-0.02em]">Your accounts</h2><p className="mt-1 text-sm text-muted-foreground">Balances returned by your banking service.</p></div>
            </div>
            <AccountList />
          </section>
          <section className="mt-12" aria-labelledby="actions-heading">
            <h2 id="actions-heading" className="text-lg font-semibold">Quick actions</h2>
            <div className="mt-4 grid gap-3 sm:grid-cols-3">
              <Button variant="outline" className="h-auto justify-start p-4" disabled><ArrowRightLeft className="size-5" /> Transfer <span className="ml-auto text-xs text-muted-foreground">Soon</span></Button>
              <Button variant="outline" className="h-auto justify-start p-4" disabled><ArrowDownLeft className="size-5" /> Deposit <span className="ml-auto text-xs text-muted-foreground">Soon</span></Button>
              <Button variant="outline" className="h-auto justify-start p-4" disabled><ArrowUpRight className="size-5" /> Withdraw <span className="ml-auto text-xs text-muted-foreground">Soon</span></Button>
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}
