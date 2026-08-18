import { BookCatalog } from "@/components/book-catalog"

export default function Home() {
  return (
    <main className="mx-auto w-full max-w-2xl flex-1 px-4 py-10">
      <h1 className="text-2xl font-semibold tracking-tight">Catálogo</h1>
      <p className="mt-1 mb-8 text-muted-foreground">
        Buscá un libro por su título y mirá si hay copias disponibles.
      </p>
      <BookCatalog />
    </main>
  )
}
