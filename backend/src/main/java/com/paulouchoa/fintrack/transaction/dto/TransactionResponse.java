package com.paulouchoa.fintrack.transaction.dto;

import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate date,
        TransactionType type,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        String categoryColor) {

    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getDescription(),
                tx.getAmount(),
                tx.getDate(),
                tx.getType(),
                tx.getAccount().getId(),
                tx.getAccount().getName(),
                tx.getCategory().getId(),
                tx.getCategory().getName(),
                tx.getCategory().getColor());
    }
}
