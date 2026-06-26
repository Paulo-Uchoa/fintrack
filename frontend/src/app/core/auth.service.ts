import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API_URL } from './api';
import { AuthResponse } from './models';

const TOKEN_KEY = 'fintrack.token';
const NAME_KEY = 'fintrack.name';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly userName = signal<string | null>(localStorage.getItem(NAME_KEY));
  readonly isAuthenticated = computed(() => this.token() !== null);

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/auth/login`, { email, password })
      .pipe(tap((res) => this.store(res)));
  }

  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/auth/register`, { name, email, password })
      .pipe(tap((res) => this.store(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(NAME_KEY);
    this.token.set(null);
    this.userName.set(null);
  }

  getToken(): string | null {
    return this.token();
  }

  private store(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(NAME_KEY, res.name);
    this.token.set(res.token);
    this.userName.set(res.name);
  }
}
