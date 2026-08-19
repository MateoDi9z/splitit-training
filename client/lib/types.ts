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