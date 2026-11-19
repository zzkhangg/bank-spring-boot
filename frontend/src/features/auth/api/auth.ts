import { apiClient } from "@/lib/api/client";
import type { AuthTokens } from "../model/session";

export interface LoginRequest {
  email: string;
  password: string;
}

export async function login(request: LoginRequest) {
  const { data } = await apiClient.post<AuthTokens>("/api/auth/customer/login", request);
  return data;
}

export async function logout(refreshToken: string) {
  await apiClient.post<void>("/api/auth/logout", { refreshToken });
}
