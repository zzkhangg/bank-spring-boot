import { LoginForm } from "@/features/auth/components/login-form";
import { useSession } from "@/features/auth/model/use-session";
import { Landmark, LockKeyhole } from "lucide-react";
import { Link, Navigate } from "react-router-dom";

export function LoginPage() {
  const tokens = useSession();
  if (tokens) return <Navigate to="/" replace />;
  return (
    <main className="grid min-h-screen lg:grid-cols-[minmax(0,1.15fr)_minmax(420px,0.85fr)]">
      <section className="relative hidden overflow-hidden bg-ink p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <div className="statement-lines absolute inset-y-0 right-14 w-px opacity-30" aria-hidden />
        <div className="flex items-center gap-3 font-semibold"><Landmark className="size-6" /> Meridian Bank</div>
        <div className="relative max-w-xl pb-12">
          <p className="mb-8 text-sm text-white/60">A clearer view of your money</p>
          <h1 className="text-balance text-5xl font-semibold leading-[1.08] tracking-[-0.035em]">Banking built around what you need to know now.</h1>
          <p className="mt-6 max-w-md text-lg leading-8 text-white/70">Review balances and activity without the noise. Every action stays connected to your secure banking profile.</p>
        </div>
        <p className="text-sm text-white/50">Private session · Encrypted connection</p>
      </section>
      <section className="flex items-center justify-center px-6 py-12 sm:px-12">
        <div className="w-full max-w-sm">
          <div className="mb-10 flex items-center gap-3 font-semibold lg:hidden"><Landmark className="size-6" /> Meridian Bank</div>
          <div className="mb-8">
            <div className="mb-5 grid size-11 place-items-center rounded-full bg-accent"><LockKeyhole className="size-5 text-accent-foreground" /></div>
            <h2 className="text-3xl font-semibold tracking-[-0.025em]">Welcome back</h2>
            <p className="mt-2 text-muted-foreground">Sign in with your customer credentials.</p>
          </div>
          <LoginForm />

          <Link to="/register">Create an account</Link>
          
          <p className="mt-8 text-center text-xs leading-5 text-muted-foreground">Your session is kept in memory and clears when this tab closes or reloads.</p>
        </div>
      </section>
    </main>
  );
}
