package com.paulouchoa.fintrack.budget.dto;

import com.paulouchoa.fintrack.budget.Budget;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public record BudgetResponse(
        Long id,
        String month,
        Long categoryId,
        String categoryName,
        String color,
        BigDecimal limitAmount,
        BigDecimal spent,
        BigDecimal remaining) {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static BudgetResponse from(Budget budget, BigDecimal spent) {
        return new BudgetResponse(
                budget.getId(),
                budget.getReferenceMonth().format(MONTH_FORMAT),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getCategory().getColor(),
                budget.getLimitAmount(),
                spent,
                budget.getLimitAmount().subtract(spent));
    }
}
