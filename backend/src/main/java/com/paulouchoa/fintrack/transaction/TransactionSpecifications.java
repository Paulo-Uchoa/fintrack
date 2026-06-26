package com.paulouchoa.fintrack.transaction;

import com.paulouchoa.fintrack.common.TransactionType;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
public final class TransactionSpecifications {

    private TransactionSpecifications() {
    }

    public static Specification<Transaction> ofUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Transaction> dateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), from);
    }

    public static Specification<Transaction> dateTo(LocalDate to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), to);
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        if (type == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasAccount(Long accountId) {
        if (accountId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("account").get("id"), accountId);
    }

    public static Specification<Transaction> hasCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }
}
