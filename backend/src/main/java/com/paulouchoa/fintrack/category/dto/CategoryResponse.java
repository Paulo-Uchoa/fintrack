package com.paulouchoa.fintrack.category.dto;

import com.paulouchoa.fintrack.category.Category;
import com.paulouchoa.fintrack.common.TransactionType;

public record CategoryResponse(
        Long id,
        String name,
        TransactionType type,
        String color) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType(), category.getColor());
    }
}
