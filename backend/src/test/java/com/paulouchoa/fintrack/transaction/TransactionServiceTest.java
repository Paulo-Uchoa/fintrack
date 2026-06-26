package com.paulouchoa.fintrack.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paulouchoa.fintrack.account.Account;
import com.paulouchoa.fintrack.account.AccountRepository;
import com.paulouchoa.fintrack.account.AccountType;
import com.paulouchoa.fintrack.category.Category;
import com.paulouchoa.fintrack.category.CategoryRepository;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.common.NotFoundException;
import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.support.TestFixtures;
import com.paulouchoa.fintrack.transaction.dto.TransactionRequest;
import com.paulouchoa.fintrack.transaction.dto.TransactionResponse;
import com.paulouchoa.fintrack.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final Long USER_ID = 3L;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequest request(TransactionType type) {
        return new TransactionRequest("Lunch", new BigDecimal("25.00"), LocalDate.now(), type, 1L, 2L);
    }

    @Test
    void create_persistsTransaction_whenCategoryTypeMatches() {
        Account account = new Account("Checking", AccountType.CHECKING, BigDecimal.ZERO, TestFixtures.user());
        Category category = new Category("Food", TransactionType.EXPENSE, "#e67e22", TestFixtures.user());
        when(accountRepository.findByIdAndUserId(1L, USER_ID)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(2L, USER_ID)).thenReturn(Optional.of(category));
        when(userRepository.getReferenceById(USER_ID)).thenReturn(TestFixtures.user());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.create(request(TransactionType.EXPENSE), USER_ID);

        assertThat(response.description()).isEqualTo("Lunch");
        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_throwsBusinessException_whenCategoryTypeDiffersFromTransactionType() {
        Account account = new Account("Checking", AccountType.CHECKING, BigDecimal.ZERO, TestFixtures.user());
        Category incomeCategory = new Category("Salary", TransactionType.INCOME, "#2ecc71", TestFixtures.user());
        when(accountRepository.findByIdAndUserId(1L, USER_ID)).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(2L, USER_ID)).thenReturn(Optional.of(incomeCategory));

        assertThatThrownBy(() -> transactionService.create(request(TransactionType.EXPENSE), USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("does not match");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFound_whenAccountDoesNotBelongToUser() {
        when(accountRepository.findByIdAndUserId(1L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(request(TransactionType.EXPENSE), USER_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Account not found");
    }
}
