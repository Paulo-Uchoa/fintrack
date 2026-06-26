package com.paulouchoa.fintrack.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paulouchoa.fintrack.account.dto.AccountRequest;
import com.paulouchoa.fintrack.account.dto.AccountResponse;
import com.paulouchoa.fintrack.common.BusinessException;
import com.paulouchoa.fintrack.common.NotFoundException;
import com.paulouchoa.fintrack.support.TestFixtures;
import com.paulouchoa.fintrack.transaction.TransactionRepository;
import com.paulouchoa.fintrack.user.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final Long USER_ID = 7L;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void create_persistsAccount_whenNameIsUnique() {
        AccountRequest request = new AccountRequest("Checking", AccountType.CHECKING, new BigDecimal("100.00"));
        when(accountRepository.existsByUserIdAndNameIgnoreCase(USER_ID, "Checking")).thenReturn(false);
        when(userRepository.getReferenceById(USER_ID)).thenReturn(TestFixtures.user());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.create(request, USER_ID);

        assertThat(response.name()).isEqualTo("Checking");
        assertThat(response.type()).isEqualTo(AccountType.CHECKING);
        assertThat(response.initialBalance()).isEqualByComparingTo("100.00");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void create_throwsBusinessException_whenNameAlreadyExists() {
        AccountRequest request = new AccountRequest("Wallet", AccountType.WALLET, BigDecimal.ZERO);
        when(accountRepository.existsByUserIdAndNameIgnoreCase(USER_ID, "Wallet")).thenReturn(true);

        assertThatThrownBy(() -> accountService.create(request, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void delete_throwsBusinessException_whenAccountHasTransactions() {
        Account account = new Account("Checking", AccountType.CHECKING, BigDecimal.ZERO, TestFixtures.user());
        when(accountRepository.findByIdAndUserId(1L, USER_ID)).thenReturn(Optional.of(account));
        when(transactionRepository.existsByAccountId(any())).thenReturn(true);

        assertThatThrownBy(() -> accountService.delete(1L, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("archive it instead");
        verify(accountRepository, never()).delete(any());
    }

    @Test
    void get_throwsNotFound_whenAccountMissing() {
        when(accountRepository.findByIdAndUserId(99L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.get(99L, USER_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_neverChecksDuplicate_withDifferentUserScope() {
        AccountRequest request = new AccountRequest("Savings", AccountType.SAVINGS, BigDecimal.ZERO);
        when(accountRepository.existsByUserIdAndNameIgnoreCase(anyLong(), anyString())).thenReturn(false);
        when(userRepository.getReferenceById(anyLong())).thenReturn(TestFixtures.user());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request, USER_ID);

        verify(accountRepository).existsByUserIdAndNameIgnoreCase(USER_ID, "Savings");
    }
}
