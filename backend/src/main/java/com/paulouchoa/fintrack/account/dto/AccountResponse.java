package com.paulouchoa.fintrack.account.dto;

import com.paulouchoa.fintrack.account.Account;
import com.paulouchoa.fintrack.account.AccountType;
import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal initialBalance,
        boolean archived) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getInitialBalance(),
                account.isArchived());
    }
}
