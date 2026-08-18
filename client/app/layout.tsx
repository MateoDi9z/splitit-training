import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { BookOpen } from "lucide-react";
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

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="es"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col">
        <header className="border-b">
          <div className="mx-auto flex h-14 w-full max-w-2xl items-center gap-2 px-4">
            <BookOpen className="size-4" aria-hidden />
            <span className="text-sm font-medium tracking-tight">Biblioteca</span>
          </div>
        </header>
        {children}
      </body>
    </html>
  );
}
