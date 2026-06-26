package com.paulouchoa.fintrack.report;

import java.math.BigDecimal;

public interface CategorySummaryProjection {

    Long getCategoryId();

    String getCategoryName();

    String getColor();

    BigDecimal getTotal();
}
