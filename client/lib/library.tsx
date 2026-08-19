"use client"

import { useEffect, useState } from "react"
import {
  ApiError,
  createLoan,
  getBook,
  getBooks,
  getLoans,
} from "@/lib/api"

import type { Book, Loan } from "@/lib/types"
import { isLoanActive } from "@/lib/books"

function errorMessage(error: unknown, fallback: string) {
  if (error instanceof ApiError) {
    if (error.status === 404) return fallback
    if (error.message && !error.message.startsWith("No static resource")) {
      return error.message
    }
  }
  if (error instanceof Error && error.message) return error.message
  return fallback
}

function activeLoanFor(loans: Loan[], bookId: number) {
  return loans.find((item) => item.bookId === bookId && isLoanActive(item)) ?? null
}

export function useBooks() {
  const [books, setBooks] = useState<Book[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    getBooks()
      .then((data) => {
        if (cancelled) return
        setBooks(data)
        setError(null)
      })
      .catch((err: unknown) => {
        if (cancelled) return
        setBooks([])
        setError(errorMessage(err, "No pudimos cargar el catálogo."))
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  return { books, loading, error }
}

export function useBook(bookId: number) {
  const [book, setBook] = useState<Book | null>(null)
  const [loan, setLoan] = useState<Loan | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [notFound, setNotFound] = useState(false)
  const [reserving, setReserving] = useState(false)
  const [reserveError, setReserveError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    Promise.allSettled([getBook(bookId), getLoans()])
      .then(([bookResult, loansResult]) => {
        if (cancelled) return

        if (bookResult.status === "fulfilled") {
          setBook(bookResult.value)
          setNotFound(false)
          setError(null)
        } else {
          const err = bookResult.reason
          setBook(null)
          if (err instanceof ApiError && err.status === 404) {
            setNotFound(true)
            setError(null)
          } else {
            setNotFound(false)
            setError(errorMessage(err, "No pudimos cargar el libro."))
          }
        }

        if (loansResult.status === "fulfilled") {
          setLoan(activeLoanFor(loansResult.value, bookId))
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [bookId])

  async function reserve() {
    setReserving(true)
    setReserveError(null)

    try {
      const created = await createLoan(bookId)
      setLoan(created)
      try {
        setBook(await getBook(bookId))
      } catch {
        setBook((current) =>
          current
            ? {
                ...current,
                availableCopies: Math.max(0, current.availableCopies - 1),
              }
            : current
        )
      }
    } catch (err) {
      setReserveError(errorMessage(err, "No se pudo reservar el libro."))
    } finally {
      setReserving(false)
    }
  }

  return {
    book,
    loan,
    loading,
    error,
    notFound,
    reserving,
    reserveError,
    reserve,
  }
}
