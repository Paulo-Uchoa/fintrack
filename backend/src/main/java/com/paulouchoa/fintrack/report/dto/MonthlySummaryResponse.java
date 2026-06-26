package com.paulouchoa.fintrack.report.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategorySummary> incomeByCategory,
        List<CategorySummary> expenseByCategory) {
}
