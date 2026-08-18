"use client"

import { useState } from "react"
import { Search } from "lucide-react"
import { cn } from "@/lib/utils"
import { books, filterBooksByTitle } from "@/lib/books"
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Input } from "@/components/ui/input"

export function BookCatalog() {
  const [query, setQuery] = useState("")
  const results = filterBooksByTitle(query, books)

  return (
    <div className="space-y-6">
      <form role="search" onSubmit={(event) => event.preventDefault()}>
        <label htmlFor="book-search" className="sr-only">
          Buscar libro por título
        </label>
        <div className="relative">
          <Search className="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            id="book-search"
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por título..."
            autoComplete="off"
            className="h-10 pl-9"
          />
        </div>
      </form>

      {results.length === 0 ? (
        <p className="text-sm text-muted-foreground">
          No hay libros que coincidan con “{query.trim()}”.
        </p>
      ) : (
        <ul className="grid gap-3">
          {results.map((book) => {
            const available = book.availableCopies > 0

            return (
              <li key={book.id}>
                <Card>
                  <CardHeader className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                    <div className="space-y-1">
                      <CardTitle>{book.title}</CardTitle>
                      <CardDescription>{book.author}</CardDescription>
                    </div>
                    <span
                      className={cn(
                        "text-sm sm:shrink-0",
                        !available && "text-muted-foreground"
                      )}
                    >
                      {available
                        ? `${book.availableCopies} de ${book.totalCopies} disponibles`
                        : "Sin copias"}
                    </span>
                  </CardHeader>
                </Card>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}
