export type Book = {
  id: number
  title: string
  author: string
  totalCopies: number
  availableCopies: number
}

export type Loan = {
  id: number
  bookId: number
  dueDate: string
  returnedAt: string | null
}

export function getBooks() {
  return request<Book[]>("/api/books")
}

export function getBook(id: number) {
  return request<Book>(`/api/books/${id}`)
}
