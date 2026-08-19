"use client"

import Link from "next/link"
import { formatDueDate } from "@/lib/books"
import { useBook } from "@/lib/library"
import { Button } from "@/components/ui/button"

export function BookDetail({ bookId }: { bookId: number }) {
  const { book, loan, loading, error, notFound, reserving, reserveError, reserve } =
    useBook(bookId)

  if (loading) {
    return <p className="text-sm text-muted-foreground">Cargando libro...</p>
  }

  if (notFound || !book) {
    return (
      <div className="space-y-8">
        <Link
          href="/"
          className="text-sm text-muted-foreground transition-colors hover:text-foreground"
        >
          ← Catálogo
        </Link>
        <p className="text-sm text-muted-foreground">
          {error ?? "No encontramos ese libro."}
        </p>
      </div>
    )
  }

  const available = book.availableCopies > 0

  return (
    <div className="space-y-8">
      <Link
        href="/"
        className="text-sm text-muted-foreground transition-colors hover:text-foreground"
      >
        ← Catálogo
      </Link>

      <div className="space-y-1">
        <h1 className="text-2xl font-semibold tracking-tight">{book.title}</h1>
        <p className="text-muted-foreground">{book.author}</p>
      </div>

      <p className={available ? "text-sm" : "text-sm text-muted-foreground"}>
        {available
          ? `${book.availableCopies} de ${book.totalCopies} disponibles`
          : "Sin copias"}
      </p>

      {loan ? (
        <p className="text-sm">
          Reservado. Devolvélo antes del {formatDueDate(loan.dueDate)}.
        </p>
      ) : (
        <div className="space-y-3">
          <Button
            size="lg"
            disabled={!available || reserving}
            onClick={() => {
              void reserve()
            }}
          >
            {reserving ? "Reservando..." : available ? "Reservar" : "Sin copias"}
          </Button>
          {reserveError ? (
            <p className="text-sm text-muted-foreground">{reserveError}</p>
          ) : null}
        </div>
      )}
    </div>
  )
}
