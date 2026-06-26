import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FinanceService, TransactionQuery } from '../../core/finance.service';
import { Account, Category, Transaction, TransactionType } from '../../core/models';

@Component({
  selector: 'app-transactions',
  imports: [ReactiveFormsModule],
  templateUrl: './transactions.html',
  styleUrl: './transactions.scss',
})
export class Transactions {
  private readonly fb = inject(FormBuilder);
  private readonly finance = inject(FinanceService);

  readonly accounts = signal<Account[]>([]);
  readonly categories = signal<Category[]>([]);
  readonly transactions = signal<Transaction[]>([]);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly showForm = signal(false);

  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly filterType = signal<TransactionType | ''>('');

  readonly selectedFormType = signal<TransactionType>('EXPENSE');
  readonly formCategories = computed(() =>
    this.categories().filter((c) => c.type === this.selectedFormType()),
  );

  readonly form = this.fb.nonNullable.group({
    description: ['', [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    date: [new Date().toISOString().slice(0, 10), [Validators.required]],
    type: ['EXPENSE' as TransactionType, [Validators.required]],
    accountId: [null as number | null, [Validators.required]],
    categoryId: [null as number | null, [Validators.required]],
  });

  constructor() {
    this.form.controls.type.valueChanges.subscribe((type) => {
      this.selectedFormType.set(type);
      this.form.controls.categoryId.reset(null);
    });

    this.finance.listAccounts().subscribe((a) => this.accounts.set(a));
    this.finance.listCategories().subscribe((c) => this.categories.set(c));
    this.load(0);
  }

  load(page: number): void {
    this.loading.set(true);
    const query: TransactionQuery = { page, size: 10 };
    if (this.filterType()) {
      query.type = this.filterType() as TransactionType;
    }
    this.finance.listTransactions(query).subscribe({
      next: (res) => {
        this.transactions.set(res.content);
        this.page.set(res.page);
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load transactions.');
        this.loading.set(false);
      },
    });
  }

  onFilterChange(value: string): void {
    this.filterType.set(value as TransactionType | '');
    this.load(0);
  }

  toggleForm(): void {
    this.showForm.update((v) => !v);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    const raw = this.form.getRawValue();
    this.finance
      .createTransaction({
        description: raw.description,
        amount: raw.amount!,
        date: raw.date,
        type: raw.type,
        accountId: raw.accountId!,
        categoryId: raw.categoryId!,
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.showForm.set(false);
          this.form.reset({ date: new Date().toISOString().slice(0, 10), type: 'EXPENSE' });
          this.load(0);
        },
        error: (err) => {
          this.error.set(err?.error?.message ?? 'Could not save the transaction.');
          this.saving.set(false);
        },
      });
  }

  remove(tx: Transaction): void {
    this.finance.deleteTransaction(tx.id).subscribe(() => this.load(this.page()));
  }

  money(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }
}
