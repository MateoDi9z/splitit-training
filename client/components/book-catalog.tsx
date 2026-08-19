"use client";

import { useState } from "react";
import { AlertCircleIcon, BookXIcon, SearchIcon } from "lucide-react";
import Link from "next/link";
import { filterBooksByTitle } from "@/lib/books";
import { useBooks } from "@/lib/library";
import type { Book } from "@/lib/types";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import { Field, FieldLabel } from "@/components/ui/field";
import { InputGroup, InputGroupAddon, InputGroupInput } from "@/components/ui/input-group";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemGroup,
  ItemTitle,
} from "@/components/ui/item";
import { Spinner } from "@/components/ui/spinner";

export function BookCatalog() {
  const { books, loading, error } = useBooks();
  const [query, setQuery] = useState("");
  const results = filterBooksByTitle(query, books);
  const trimmedQuery = query.trim();

  return (
    <div className="flex flex-col gap-6">
      <form role="search" onSubmit={(event) => event.preventDefault()}>
        <Field>
          <FieldLabel htmlFor="book-search" className="sr-only">
            Buscar libro por título
          </FieldLabel>
          <InputGroup>
            <InputGroupAddon>
              <SearchIcon />
            </InputGroupAddon>
            <InputGroupInput
              id="book-search"
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por título..."
              autoComplete="off"
            />
          </InputGroup>
        </Field>
      </form>

      {loading ? (
        <Empty>
          <EmptyHeader>
            <EmptyMedia variant="icon">
              <Spinner />
            </EmptyMedia>
            <EmptyTitle>Cargando catálogo</EmptyTitle>
            <EmptyDescription>Estamos buscando los libros disponibles.</EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : error ? (
        <Alert variant="destructive">
          <AlertCircleIcon />
          <AlertTitle>No se pudo cargar el catálogo</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      ) : results.length === 0 ? (
        <Empty>
          <EmptyHeader>
            <EmptyMedia variant="icon">
              <BookXIcon />
            </EmptyMedia>
            <EmptyTitle>{trimmedQuery ? "Sin resultados" : "Catálogo vacío"}</EmptyTitle>
            <EmptyDescription>
              {trimmedQuery
                ? `No hay libros que coincidan con “${trimmedQuery}”.`
                : "No hay libros en el catálogo."}
            </EmptyDescription>
          </EmptyHeader>
        </Empty>
      ) : (
        <BookList books={results} />
      )}
    </div>
  );
}

function BookList({ books }: { books: Book[] }) {
  return (
    <ItemGroup>
      {books.map((book) => {
        const available = book.availableCopies > 0;

        return (
          <Item key={book.id} variant="outline" render={<Link href={`/books/${book.id}`} />}>
            <ItemContent>
              <ItemTitle>{book.title}</ItemTitle>
              <ItemDescription>{book.author}</ItemDescription>
            </ItemContent>
            <ItemActions>
              <Badge variant={available ? "secondary" : "outline"}>
                {available
                  ? `${book.availableCopies} de ${book.totalCopies} disponibles`
                  : "Sin copias"}
              </Badge>
            </ItemActions>
          </Item>
        );
      })}
    </ItemGroup>
  );
}
