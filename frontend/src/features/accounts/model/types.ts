export type AccountStatus = "ACTIVE" | "FROZEN" | "CLOSED";

export interface AccountSummary {
  accountId: string;
  accountNumber: string;
  balance: number;
  status: AccountStatus;
}
