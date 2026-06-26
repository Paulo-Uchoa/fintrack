package com.paulouchoa.fintrack.category.dto;

import com.paulouchoa.fintrack.common.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 80) String name,
        @NotNull TransactionType type,
        @Pattern(regexp = "^#([0-9a-fA-F]{6})$", message = "color must be a hex code like #1abc9c") String color) {
}
