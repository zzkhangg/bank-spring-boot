import { createBrowserRouter } from "react-router-dom";
import { AppShell } from "./app-shell";
import { ProtectedRoute } from "./protected-route";
import { DashboardPage } from "@/pages/dashboard-page";
import { LoginPage } from "@/pages/login-page";
import { NotFoundPage } from "@/pages/not-found-page";

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [{ element: <AppShell />, children: [{ index: true, element: <DashboardPage /> }] }],
  },
  { path: "*", element: <NotFoundPage /> },
]);
