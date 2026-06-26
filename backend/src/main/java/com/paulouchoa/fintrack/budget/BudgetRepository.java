package com.paulouchoa.fintrack.budget;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndReferenceMonth(Long userId, LocalDate referenceMonth);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCategoryIdAndReferenceMonth(Long userId, Long categoryId, LocalDate referenceMonth);

    boolean existsByCategoryId(Long categoryId);
}
