import { Component, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { FinanceService } from '../../core/finance.service';
import { Budget, MonthlySummary } from '../../core/models';

interface DonutSegment {
  color: string;
  dash: number;
  offset: number;
  name: string;
  percent: number;
}

const CIRCUMFERENCE = 2 * Math.PI * 60;
const FALLBACK_COLORS = ['#6366f1', '#34d399', '#f87171', '#fbbf24', '#22d3ee', '#a78bfa'];

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private readonly finance = inject(FinanceService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly summary = signal<MonthlySummary | null>(null);
  readonly budgets = signal<Budget[]>([]);

  readonly circumference = CIRCUMFERENCE;

  readonly segments = computed<DonutSegment[]>(() => {
    const expenses = this.summary()?.expenseByCategory ?? [];
    const total = expenses.reduce((sum, c) => sum + c.total, 0);
    if (total === 0) {
      return [];
    }
    let offset = 0;
    return expenses.map((c, i) => {
      const percent = c.total / total;
      const dash = percent * CIRCUMFERENCE;
      const segment: DonutSegment = {
        color: c.color ?? FALLBACK_COLORS[i % FALLBACK_COLORS.length],
        dash,
        offset,
        name: c.categoryName,
        percent: Math.round(percent * 100),
      };
      offset += dash;
      return segment;
    });
  });

  constructor() {
    forkJoin({
      summary: this.finance.monthlySummary(),
      budgets: this.finance.listBudgets(),
    }).subscribe({
      next: ({ summary, budgets }) => {
        this.summary.set(summary);
        this.budgets.set(budgets);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load your dashboard.');
        this.loading.set(false);
      },
    });
  }

  money(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  budgetPercent(b: Budget): number {
    if (b.limitAmount === 0) {
      return 0;
    }
    return Math.min(100, Math.round((b.spent / b.limitAmount) * 100));
  }

  budgetColor(b: Budget): string {
    const pct = (b.spent / b.limitAmount) * 100;
    if (pct >= 100) {
      return 'var(--expense)';
    }
    if (pct >= 80) {
      return '#fbbf24';
    }
    return 'var(--income)';
  }
}
