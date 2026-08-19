"use client";

import Link from "next/link";
import { AlertCircleIcon, BookXIcon, CalendarIcon } from "lucide-react";
import { formatDueDate } from "@/lib/books";
import { useBook } from "@/lib/library";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Button, buttonVariants } from "@/components/ui/button";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import { Spinner } from "@/components/ui/spinner";

export function BookDetail({ bookId }: { bookId: number }) {
  const { book, loan, loading, error, notFound, reserving, reserveError, reserve } =
    useBook(bookId);

  if (loading) {
    return (
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <Spinner />
          </EmptyMedia>
          <EmptyTitle>Cargando libro</EmptyTitle>
        </EmptyHeader>
      </Empty>
    );
  }

  if (notFound) {
    return (
      <div className="flex flex-col gap-8">
        <CatalogBreadcrumb page="Libro" />
        <Empty>
          <EmptyHeader>
            <EmptyMedia variant="icon">
              <BookXIcon />
            </EmptyMedia>
            <EmptyTitle>No encontramos ese libro</EmptyTitle>
            <EmptyDescription>El libro no existe o ya no está en el catálogo.</EmptyDescription>
          </EmptyHeader>
          <EmptyContent>
            <Link href="/" className={buttonVariants()}>
              Volver al catálogo
            </Link>
          </EmptyContent>
        </Empty>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="flex flex-col gap-8">
        <CatalogBreadcrumb page="Libro" />
        <Alert variant="destructive">
          <AlertCircleIcon />
          <AlertTitle>No se pudo cargar el libro</AlertTitle>
          <AlertDescription>{error ?? "No pudimos cargar el libro."}</AlertDescription>
        </Alert>
      </div>
    );
  }

  const available = book.availableCopies > 0;

  return (
    <div className="flex flex-col gap-8">
      <CatalogBreadcrumb page={book.title} />

      <Card>
        <CardHeader>
          <CardTitle>{book.title}</CardTitle>
          <CardDescription>{book.author}</CardDescription>
          <CardAction>
            <Badge variant={available ? "secondary" : "outline"}>
              {available
                ? `${book.availableCopies} de ${book.totalCopies} disponibles`
                : "Sin copias"}
            </Badge>
          </CardAction>
        </CardHeader>
        {loan || reserveError ? (
          <CardContent>
            {loan ? (
              <Alert>
                <CalendarIcon />
                <AlertTitle>Reservado</AlertTitle>
                <AlertDescription>
                  Devolvélo antes del {formatDueDate(loan.dueDate)}.
                </AlertDescription>
              </Alert>
            ) : (
              <Alert variant="destructive">
                <AlertCircleIcon />
                <AlertTitle>No se pudo reservar</AlertTitle>
                <AlertDescription>{reserveError}</AlertDescription>
              </Alert>
            )}
          </CardContent>
        ) : null}
        {!loan ? (
          <CardFooter>
            <Button
              disabled={!available || reserving}
              onClick={() => {
                void reserve();
              }}
            >
              {reserving ? <Spinner data-icon="inline-start" /> : null}
              {reserving ? "Reservando..." : available ? "Reservar" : "Sin copias"}
            </Button>
          </CardFooter>
        ) : null}
      </Card>
    </div>
  );
}

function CatalogBreadcrumb({ page }: { page: string }) {
  return (
    <Breadcrumb>
      <BreadcrumbList>
        <BreadcrumbItem>
          <BreadcrumbLink render={<Link href="/" />}>Catálogo</BreadcrumbLink>
        </BreadcrumbItem>
        <BreadcrumbSeparator />
        <BreadcrumbItem>
          <BreadcrumbPage>{page}</BreadcrumbPage>
        </BreadcrumbItem>
      </BreadcrumbList>
    </Breadcrumb>
  );
}
