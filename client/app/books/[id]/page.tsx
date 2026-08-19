import { notFound } from "next/navigation"
import { BookDetail } from "@/components/book-detail"

export default async function BookPage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = await params
  const bookId = Number(id)

  if (!Number.isInteger(bookId)) {
    notFound()
  }

  return (
    <main className="mx-auto w-full max-w-2xl flex-1 px-4 py-10">
      <BookDetail key={bookId} bookId={bookId} />
    </main>
  )
}
