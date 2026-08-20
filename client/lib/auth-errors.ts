import { ApiError } from "@/lib/api";

export function authErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    if (error.status === 409) return "Ese email ya está registrado.";
    if (error.status === 401) return "Email o contraseña incorrectos.";
    if (error.message && !error.message.includes("\n")) return error.message;
  }
  if (error instanceof Error && error.message) return error.message;
  return "No pudimos completar la operación.";
}
