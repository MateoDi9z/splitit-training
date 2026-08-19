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

  return <BookDetail key={bookId} bookId={bookId} />
}
