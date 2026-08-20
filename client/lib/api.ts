import { clearSession, getAuthToken } from "@/lib/auth-storage";
import type { Book, Loan, LoginResponse, User } from "./types";

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

function apiUrl(path: string) {
  const base = process.env.NEXT_PUBLIC_API_URL;
  if (!base) {
    throw new ApiError(0, "Falta configurar NEXT_PUBLIC_API_URL");
  }
  return `${base.replace(/\/$/, "")}${path}`;
}

async function readErrorMessage(response: Response) {
  const text = await response.text();
  if (!text) return response.statusText || `Error ${response.status}`;

  try {
    const body = JSON.parse(text) as { message?: string };
    if (body.message && !body.message.includes("\n")) {
      return body.message;
    }
  } catch {
    if (text.length < 180) return text;
  }

  return `Error ${response.status}`;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  const token = getAuthToken();

  try {
    response = await fetch(apiUrl(path), {
      ...init,
      headers: {
        Accept: "application/json",
        ...(init?.body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...init?.headers,
      },
    });
  } catch {
    throw new ApiError(0, "No pudimos conectar con la API.");
  }

  if (!response.ok) {
    if (response.status === 401 && !path.startsWith("/api/auth/")) {
      clearSession();
    }
    throw new ApiError(response.status, await readErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export function signUp(email: string, password: string) {
  return request<User>("/api/auth/signup", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export function login(email: string, password: string) {
  return request<LoginResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

// Books
//
export function getBooks() {
  return request<Book[]>("/api/books");
}

export function getBook(id: number) {
  return request<Book>(`/api/books/${id}`);
}

// Loans

export function getLoans() {
  return request<Loan[]>("/api/loans");
}

export function createLoan(bookId: number) {
  return request<Loan>("/api/loans", {
    method: "POST",
    body: JSON.stringify({ bookId }),
  });
}
