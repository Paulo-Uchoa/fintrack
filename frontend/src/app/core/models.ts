export type TransactionType = 'INCOME' | 'EXPENSE';
export type AccountType = 'CHECKING' | 'SAVINGS' | 'WALLET' | 'CREDIT_CARD';

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInMinutes: number;
  name: string;
  email: string;
}

export interface Account {
  id: number;
  name: string;
  type: AccountType;
  initialBalance: number;
  archived: boolean;
}

export interface Category {
  id: number;
  name: string;
  type: TransactionType;
  color: string | null;
}

export interface Transaction {
  id: number;
  description: string;
  amount: number;
  date: string;
  type: TransactionType;
  accountId: number;
  accountName: string;
  categoryId: number;
  categoryName: string;
  categoryColor: string | null;
}

export interface TransactionInput {
  description: string;
  amount: number;
  date: string;
  type: TransactionType;
  accountId: number;
  categoryId: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface CategorySummary {
  categoryId: number;
  categoryName: string;
  color: string | null;
  total: number;
}

export interface MonthlySummary {
  month: string;
  totalIncome: number;
  totalExpense: number;
  balance: number;
  incomeByCategory: CategorySummary[];
  expenseByCategory: CategorySummary[];
}

export interface Budget {
  id: number;
  month: string;
  categoryId: number;
  categoryName: string;
  color: string | null;
  limitAmount: number;
  spent: number;
  remaining: number;
}
