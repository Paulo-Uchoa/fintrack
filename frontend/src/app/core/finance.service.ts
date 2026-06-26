import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './api';
import {
  Account,
  Budget,
  Category,
  MonthlySummary,
  PageResponse,
  Transaction,
  TransactionInput,
  TransactionType,
} from './models';

export interface TransactionQuery {
  from?: string;
  to?: string;
  type?: TransactionType;
  accountId?: number;
  categoryId?: number;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class FinanceService {
  private readonly http = inject(HttpClient);

  listAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(`${API_URL}/accounts`);
  }

  listCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${API_URL}/categories`);
  }

  listTransactions(query: TransactionQuery = {}): Observable<PageResponse<Transaction>> {
    let params = new HttpParams();
    for (const [key, value] of Object.entries(query)) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, String(value));
      }
    }
    return this.http.get<PageResponse<Transaction>>(`${API_URL}/transactions`, { params });
  }

  createTransaction(input: TransactionInput): Observable<Transaction> {
    return this.http.post<Transaction>(`${API_URL}/transactions`, input);
  }

  deleteTransaction(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/transactions/${id}`);
  }

  monthlySummary(month?: string): Observable<MonthlySummary> {
    let params = new HttpParams();
    if (month) {
      params = params.set('month', month);
    }
    return this.http.get<MonthlySummary>(`${API_URL}/reports/summary`, { params });
  }

  listBudgets(month?: string): Observable<Budget[]> {
    let params = new HttpParams();
    if (month) {
      params = params.set('month', month);
    }
    return this.http.get<Budget[]>(`${API_URL}/budgets`, { params });
  }
}
