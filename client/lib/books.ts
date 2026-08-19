import type { Book, Loan } from "@/lib/types"

export type { Book, Loan }

function normalize(value: string) {
  return value.trim().toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
}

export function filterBooksByTitle(query: string, catalog: Book[]) {
  const q = normalize(query)
  if (!q) return catalog
  return catalog.filter((book) => normalize(book.title).includes(q))
}

export function isLoanActive(loan: Loan) {
  return loan.returnedAt == null
}

export function formatDueDate(value: string) {
  const date = new Date(/^\d{4}-\d{2}-\d{2}$/.test(value) ? `${value}T12:00:00` : value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleDateString("es-AR", {
    day: "numeric",
    month: "long",
    year: "numeric",
  })
}
