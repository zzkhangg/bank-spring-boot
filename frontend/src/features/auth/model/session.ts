export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

type Listener = () => void;

let tokens: AuthTokens | null = null;
const listeners = new Set<Listener>();

export const session = {
  get: () => tokens,
  set(next: AuthTokens | null) {
    tokens = next;
    listeners.forEach((listener) => listener());
  },
  subscribe(listener: Listener) {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
};
