import { Landmark, LogOut, Menu } from "lucide-react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { logout } from "@/features/auth/api/auth";
import { session } from "@/features/auth/model/session";

export function AppShell() {
  const navigate = useNavigate();
  const signOut = async () => {
    const refreshToken = session.get()?.refreshToken;
    session.set(null);
    navigate("/login", { replace: true });
    if (refreshToken) await logout(refreshToken).catch(() => undefined);
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b border-border bg-background/95">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-5 lg:px-8">
          <NavLink to="/" className="flex items-center gap-3 font-semibold">
            <span className="grid size-9 place-items-center rounded-md bg-primary text-primary-foreground"><Landmark className="size-5" /></span>
            Meridian
          </NavLink>
          <nav className="hidden items-center gap-8 text-sm md:flex" aria-label="Primary">
            <NavLink to="/" className={({ isActive }) => isActive ? "font-semibold text-foreground" : "text-muted-foreground"}>Overview</NavLink>
            <span className="cursor-not-allowed text-muted-foreground/55" title="Planned feature">Payments</span>
            <span className="cursor-not-allowed text-muted-foreground/55" title="Planned feature">Activity</span>
          </nav>
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="sm" onClick={signOut}><LogOut className="size-4" /> <span className="hidden sm:inline">Sign out</span></Button>
            <Button variant="ghost" size="icon" className="md:hidden" aria-label="Open navigation"><Menu className="size-5" /></Button>
          </div>
        </div>
      </header>
      <Outlet />
    </div>
  );
}
