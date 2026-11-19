import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { session, type AuthTokens } from "@/features/auth/model/session";

interface RetryConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const baseURL = import.meta.env.VITE_API_BASE_URL || "/";

export const apiClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
  timeout: 15_000,
});

apiClient.interceptors.request.use((config) => {
  const accessToken = session.get()?.accessToken;
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  return config;
});

let refreshPromise: Promise<AuthTokens> | null = null;

apiClient.interceptors.response.use(undefined, async (error: AxiosError) => {
  const request = error.config as RetryConfig | undefined;
  const refreshToken = session.get()?.refreshToken;
  const isAuthCall = request?.url?.startsWith("/api/auth/");

  if (error.response?.status !== 401 || !request || request._retry || !refreshToken || isAuthCall) {
    return Promise.reject(error);
  }

  request._retry = true;
  refreshPromise ??= axios
    .post<AuthTokens>("/api/auth/refresh", { refreshToken }, { baseURL })
    .then(({ data }) => {
      session.set(data);
      return data;
    })
    .catch((refreshError: unknown) => {
      session.set(null);
      throw refreshError;
    })
    .finally(() => {
      refreshPromise = null;
    });

  const nextTokens = await refreshPromise;
  request.headers.Authorization = `Bearer ${nextTokens.accessToken}`;
  return apiClient(request);
});

export function getApiErrorMessage(error: unknown, fallback = "The request could not be completed.") {
  if (!axios.isAxiosError(error)) return fallback;
  const message = (error.response?.data as { message?: unknown } | undefined)?.message;
  return typeof message === "string" && message.length > 0 ? message : fallback;
}
