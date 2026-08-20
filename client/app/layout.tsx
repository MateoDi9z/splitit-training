import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { BookOpen } from "lucide-react";
import Link from "next/link";
import { cookies } from "next/headers";
import { AuthProvider } from "@/lib/auth";
import { AUTH_TOKEN_COOKIE, decodeUserFromToken } from "@/lib/auth-storage";
import { AuthStatus } from "@/components/auth-status";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Biblioteca",
  description: "Préstamo de libros de la biblioteca",
};

export default async function RootLayout({ children }: LayoutProps<"/">) {
  const token = (await cookies()).get(AUTH_TOKEN_COOKIE)?.value ?? null;
  const initialUser = token ? decodeUserFromToken(token) : null;

  return (
    <html lang="es" className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}>
      <body className="flex min-h-full flex-col">
        <AuthProvider initialUser={initialUser}>
          <header className="border-b">
            <div className="mx-auto flex h-14 w-full max-w-2xl items-center justify-between gap-4 px-4">
              <Link href="/" className="flex items-center gap-2 text-sm font-medium tracking-tight">
                <BookOpen className="size-4" aria-hidden />
                Biblioteca
              </Link>
              <AuthStatus />
            </div>
          </header>
          <main className="mx-auto flex w-full max-w-2xl flex-1 flex-col px-4 py-10">
            {children}
          </main>
        </AuthProvider>
      </body>
    </html>
  );
}
