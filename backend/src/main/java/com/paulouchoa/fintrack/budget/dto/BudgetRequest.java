package com.paulouchoa.fintrack.budget.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetRequest(
        @NotNull YearMonth month,
        @NotNull Long categoryId,
        @NotNull @Positive BigDecimal limitAmount) {
}
