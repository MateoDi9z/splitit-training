"use client";

import { createContext, useCallback, useContext, useMemo, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { login as loginRequest, signUp as signUpRequest } from "@/lib/api";
import { clearSession, getStoredUser, setSession, subscribeToAuth } from "@/lib/auth-storage";
import type { User } from "@/lib/types";

type AuthContextValue = {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({
  children,
  initialUser,
}: {
  children: React.ReactNode;
  initialUser: User | null;
}) {
  const router = useRouter();
  const user = useSyncExternalStore(subscribeToAuth, getStoredUser, () => initialUser);

  const login = useCallback(async (email: string, password: string) => {
    const result = await loginRequest(email, password);
    setSession(result.token, result.user);
  }, []);

  const signup = useCallback(
    async (email: string, password: string) => {
      await signUpRequest(email, password);
      await login(email, password);
    },
    [login]
  );

  const logout = useCallback(() => {
    clearSession();
    router.push("/login");
    router.refresh();
  }, [router]);

  const value = useMemo(() => ({ user, login, signup, logout }), [user, login, signup, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
