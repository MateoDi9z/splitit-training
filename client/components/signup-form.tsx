"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { AlertCircleIcon } from "lucide-react";
import { useAuth } from "@/lib/auth";
import { authErrorMessage } from "@/lib/auth-errors";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Field, FieldError, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Spinner } from "@/components/ui/spinner";

const signupSchema = z.object({
  email: z.email("Ingresá un email válido"),
  password: z.string().min(8, "La contraseña debe tener al menos 8 caracteres"),
});

type SignupValues = z.infer<typeof signupSchema>;

export function SignupForm() {
  const router = useRouter();
  const { signup } = useAuth();
  const [apiError, setApiError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<SignupValues>({
    resolver: zodResolver(signupSchema),
    defaultValues: { email: "", password: "" },
  });

  async function onSubmit(values: SignupValues) {
    setApiError(null);
    try {
      await signup(values.email, values.password);
      router.push("/");
      router.refresh();
    } catch (error) {
      setApiError(authErrorMessage(error));
    }
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle>Crear cuenta</CardTitle>
        <CardDescription>Registrate como socio para pedir libros prestados.</CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit(onSubmit)}>
        <CardContent className="mb-4">
          <FieldGroup>
            {apiError ? (
              <Alert variant="destructive">
                <AlertCircleIcon />
                <AlertTitle>No se pudo registrar</AlertTitle>
                <AlertDescription>{apiError}</AlertDescription>
              </Alert>
            ) : null}
            <Field data-invalid={!!errors.email || undefined}>
              <FieldLabel htmlFor="signup-email">Email</FieldLabel>
              <Input
                id="signup-email"
                type="email"
                autoComplete="email"
                aria-invalid={!!errors.email}
                {...register("email")}
              />
              <FieldError errors={[errors.email]} />
            </Field>
            <Field data-invalid={!!errors.password || undefined}>
              <FieldLabel htmlFor="signup-password">Contraseña</FieldLabel>
              <Input
                id="signup-password"
                type="password"
                autoComplete="new-password"
                aria-invalid={!!errors.password}
                {...register("password")}
              />
              <FieldError errors={[errors.password]} />
            </Field>
          </FieldGroup>
        </CardContent>
        <CardFooter className="flex-col items-stretch gap-3">
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? <Spinner data-icon="inline-start" /> : null}
            {isSubmitting ? "Creando cuenta..." : "Registrarme"}
          </Button>
          <p className="text-muted-foreground text-center text-sm">
            ¿Ya tenés cuenta?{" "}
            <Link href="/login" className="text-foreground underline-offset-4 hover:underline">
              Iniciar sesión
            </Link>
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
