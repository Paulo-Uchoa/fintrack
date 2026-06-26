package com.paulouchoa.fintrack.transaction;

import com.paulouchoa.fintrack.common.TransactionType;
import java.time.LocalDate;

public record TransactionFilter(
        LocalDate from,
        LocalDate to,
        TransactionType type,
        Long accountId,
        Long categoryId) {
}
