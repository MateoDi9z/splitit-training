"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth";
import { Badge } from "@/components/ui/badge";
import { Button, buttonVariants } from "@/components/ui/button";

function roleLabel(role: string) {
  if (role === "LIBRARIAN") return "Bibliotecario";
  return "Socio";
}

export function AuthStatus() {
  const { user, logout } = useAuth();

  if (!user) {
    return (
      <div className="flex items-center gap-3 text-sm">
        <span className="text-muted-foreground hidden sm:inline">No estás logueado</span>
        <Link href="/login" className="hover:underline">
          Iniciar sesión
        </Link>
        <Link href="/signup" className={buttonVariants({ size: "sm" })}>
          Registrarse
        </Link>
      </div>
    );
  }

  return (
    <div className="flex min-w-0 items-center gap-2 text-sm">
      <div className="min-w-0 text-right">
        <p className="truncate font-medium">{user.email}</p>
        <p className="text-muted-foreground text-xs">Logueado</p>
      </div>
      <Badge variant="secondary">{roleLabel(user.role)}</Badge>
      <Button variant="outline" size="sm" onClick={logout}>
        Salir
      </Button>
    </div>
  );
}
