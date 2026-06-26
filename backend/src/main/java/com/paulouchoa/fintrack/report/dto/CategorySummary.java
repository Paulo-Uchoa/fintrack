package com.paulouchoa.fintrack.report.dto;

import com.paulouchoa.fintrack.report.CategorySummaryProjection;
import java.math.BigDecimal;

public record CategorySummary(
        Long categoryId,
        String categoryName,
        String color,
        BigDecimal total) {

    public static CategorySummary from(CategorySummaryProjection projection) {
        return new CategorySummary(
                projection.getCategoryId(),
                projection.getCategoryName(),
                projection.getColor(),
                projection.getTotal());
    }
}
