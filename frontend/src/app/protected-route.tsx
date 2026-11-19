import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useSession } from "@/features/auth/model/use-session";

export function ProtectedRoute() {
  const tokens = useSession();
  const location = useLocation();
  if (!tokens) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  return <Outlet />;
}
