package com.paulouchoa.fintrack.transaction;

import com.paulouchoa.fintrack.account.Account;
import com.paulouchoa.fintrack.account.AccountRepository;
import com.paulouchoa.fintrack.category.Category;
import com.paulouchoa.fintrack.category.CategoryRepository;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.common.NotFoundException;
import com.paulouchoa.fintrack.common.PageResponse;
import com.paulouchoa.fintrack.transaction.dto.TransactionRequest;
import com.paulouchoa.fintrack.transaction.dto.TransactionResponse;
import com.paulouchoa.fintrack.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> list(Long userId, TransactionFilter filter, Pageable pageable) {
        Specification<Transaction> spec = Specification.allOf(
                TransactionSpecifications.ofUser(userId),
                TransactionSpecifications.dateFrom(filter.from()),
                TransactionSpecifications.dateTo(filter.to()),
                TransactionSpecifications.hasType(filter.type()),
                TransactionSpecifications.hasAccount(filter.accountId()),
                TransactionSpecifications.hasCategory(filter.categoryId()));
        return PageResponse.of(transactionRepository.findAll(spec, pageable), TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(Long id, Long userId) {
        return TransactionResponse.from(require(id, userId));
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request, Long userId) {
        Account account = requireAccount(request.accountId(), userId);
        Category category = requireCategory(request.categoryId(), userId);
        validateTypeMatch(request, category);

        Transaction transaction = new Transaction(request.description(), request.amount(), request.date(),
                request.type(), account, category, userRepository.getReferenceById(userId));
        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request, Long userId) {
        Transaction transaction = require(id, userId);
        Account account = requireAccount(request.accountId(), userId);
        Category category = requireCategory(request.categoryId(), userId);
        validateTypeMatch(request, category);

        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setType(request.type());
        transaction.setAccount(account);
        transaction.setCategory(category);
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        transactionRepository.delete(require(id, userId));
    }

    private void validateTypeMatch(TransactionRequest request, Category category) {
        if (category.getType() != request.type()) {
            throw new BusinessException("Category type does not match the transaction type");
        }
    }

    private Transaction require(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + id));
    }

    private Account requireAccount(Long id, Long userId) {
        return accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    }

    private Category requireCategory(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }
}
