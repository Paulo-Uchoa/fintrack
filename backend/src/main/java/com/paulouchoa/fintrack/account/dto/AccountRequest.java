package com.paulouchoa.fintrack.account.dto;

import com.paulouchoa.fintrack.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull AccountType type,
        @NotNull BigDecimal initialBalance) {
}
