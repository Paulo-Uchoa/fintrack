package com.paulouchoa.fintrack.budget;

import com.paulouchoa.fintrack.budget.dto.BudgetRequest;
import com.paulouchoa.fintrack.budget.dto.BudgetResponse;
import com.paulouchoa.fintrack.category.Category;
import com.paulouchoa.fintrack.category.CategoryRepository;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.common.NotFoundException;
import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.transaction.TransactionRepository;
import com.paulouchoa.fintrack.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(Long userId, YearMonth month) {
        return budgetRepository.findByUserIdAndReferenceMonth(userId, month.atDay(1)).stream()
                .map(budget -> toResponse(budget, userId))
                .sorted(Comparator.comparing(BudgetResponse::categoryName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public BudgetResponse create(BudgetRequest request, Long userId) {
        Category category = requireCategory(request.categoryId(), userId);
        if (category.getType() != TransactionType.EXPENSE) {
            throw new BusinessException("Budgets can only target expense categories");
        }
        LocalDate referenceMonth = request.month().atDay(1);
        if (budgetRepository.existsByUserIdAndCategoryIdAndReferenceMonth(userId, category.getId(), referenceMonth)) {
            throw new BusinessException("A budget for this category and month already exists");
        }
        Budget budget = new Budget(referenceMonth, request.limitAmount(), category,
                userRepository.getReferenceById(userId));
        return toResponse(budgetRepository.save(budget), userId);
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetRequest request, Long userId) {
        Budget budget = require(id, userId);
        budget.setLimitAmount(request.limitAmount());
        return toResponse(budget, userId);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        budgetRepository.delete(require(id, userId));
    }

    private BudgetResponse toResponse(Budget budget, Long userId) {
        YearMonth month = YearMonth.from(budget.getReferenceMonth());
        BigDecimal spent = transactionRepository.sumByCategoryAndType(
                userId, budget.getCategory().getId(), TransactionType.EXPENSE,
                month.atDay(1), month.atEndOfMonth());
        return BudgetResponse.from(budget, spent);
    }

    private Budget require(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Budget not found: " + id));
    }

    private Category requireCategory(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}
