export type Book = {
  id: number
  title: string
  author: string
  totalCopies: number
  availableCopies: number
}

export const books: Book[] = [
  { id: 1, title: "Cien años de soledad", author: "Gabriel García Márquez", totalCopies: 5, availableCopies: 3 },
  { id: 2, title: "El Aleph", author: "Jorge Luis Borges", totalCopies: 2, availableCopies: 1 },
  { id: 3, title: "Rayuela", author: "Julio Cortázar", totalCopies: 3, availableCopies: 0 },
  { id: 4, title: "Ficciones", author: "Jorge Luis Borges", totalCopies: 2, availableCopies: 2 },
  { id: 5, title: "Pedro Páramo", author: "Juan Rulfo", totalCopies: 4, availableCopies: 1 },
  { id: 6, title: "La casa de los espíritus", author: "Isabel Allende", totalCopies: 4, availableCopies: 4 },
  { id: 7, title: "El túnel", author: "Ernesto Sabato", totalCopies: 1, availableCopies: 0 },
  { id: 8, title: "Sobre héroes y tumbas", author: "Ernesto Sabato", totalCopies: 3, availableCopies: 2 },
]

function normalize(value: string) {
  return value.trim().toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "")
}

export function filterBooksByTitle(query: string, catalog: Book[] = books) {
  const q = normalize(query)
  if (!q) return catalog
  return catalog.filter((book) => normalize(book.title).includes(q))
}
