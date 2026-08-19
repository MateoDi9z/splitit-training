import { BookCatalog } from "@/components/book-catalog"

export default function Home() {
  return (
    <>
      <h1 className="text-2xl font-semibold tracking-tight">Catálogo</h1>
      <p className="mt-1 mb-8 text-muted-foreground">
        Buscá un libro por su título y mirá si hay copias disponibles.
      </p>
      <BookCatalog />
    </>
  )
}
