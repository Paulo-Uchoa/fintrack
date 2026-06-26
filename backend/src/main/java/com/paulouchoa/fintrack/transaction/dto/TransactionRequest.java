package com.paulouchoa.fintrack.transaction.dto;

import com.paulouchoa.fintrack.common.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank @Size(max = 180) String description,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate date,
        @NotNull TransactionType type,
        @NotNull Long accountId,
        @NotNull Long categoryId) {
}
