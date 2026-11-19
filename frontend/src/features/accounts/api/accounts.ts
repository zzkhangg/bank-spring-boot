import { apiClient } from "@/lib/api/client";
import type { AccountSummary } from "../model/types";

export const accountKeys = { all: ["accounts"] as const, mine: ["accounts", "mine"] as const };

export async function getMyAccounts() {
  const { data } = await apiClient.get<AccountSummary[]>("/api/accounts/myAccounts");
  return data;
}
