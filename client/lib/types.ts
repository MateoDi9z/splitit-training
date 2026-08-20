export type UserRole = "ASSOCIATE" | "LIBRARIAN";

export type User = {
  id: number;
  email: string;
  role: UserRole;
};

export type LoginResponse = {
  token: string;
  user: User;
};

export type Book = {
  id: number;
  title: string;
  author: string;
  totalCopies: number;
  availableCopies: number;
};

export type Loan = {
  id: number;
  bookId: number;
  dueDate: string;
  returnedAt: string | null;
};
