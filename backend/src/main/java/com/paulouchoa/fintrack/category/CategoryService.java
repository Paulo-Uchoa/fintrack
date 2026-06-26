package com.paulouchoa.fintrack.category;

import com.paulouchoa.fintrack.budget.BudgetRepository;
import com.paulouchoa.fintrack.category.dto.CategoryRequest;
import com.paulouchoa.fintrack.category.dto.CategoryResponse;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.common.NotFoundException;
import com.paulouchoa.fintrack.transaction.TransactionRepository;
import com.paulouchoa.fintrack.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(Long userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse get(Long id, Long userId) {
        return CategoryResponse.from(require(id, userId));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request, Long userId) {
        if (categoryRepository.existsByUserIdAndNameIgnoreCaseAndType(userId, request.name(), request.type())) {
            throw new BusinessException("A category with this name and type already exists");
        }
        Category category = new Category(request.name(), request.type(), request.color(),
                userRepository.getReferenceById(userId));
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request, Long userId) {
        Category category = require(id, userId);
        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Category category = require(id, userId);
        if (transactionRepository.existsByCategoryId(category.getId())
                || budgetRepository.existsByCategoryId(category.getId())) {
            throw new BusinessException("Category is in use by transactions or budgets");
        }
        categoryRepository.delete(category);
    }

    private Category require(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}
