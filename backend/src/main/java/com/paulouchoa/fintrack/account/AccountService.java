package com.paulouchoa.fintrack.account;

import com.paulouchoa.fintrack.account.dto.AccountRequest;
import com.paulouchoa.fintrack.account.dto.AccountResponse;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> list(Long userId) {
        return accountRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse get(Long id, Long userId) {
        return AccountResponse.from(require(id, userId));
    }

    @Transactional
    public AccountResponse create(AccountRequest request, Long userId) {
        if (accountRepository.existsByUserIdAndNameIgnoreCase(userId, request.name())) {
            throw new BusinessException("An account with this name already exists");
        }
        Account account = new Account(request.name(), request.type(), request.initialBalance(),
                userRepository.getReferenceById(userId));
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse update(Long id, AccountRequest request, Long userId) {
        Account account = require(id, userId);
        account.setName(request.name());
        account.setType(request.type());
        account.setInitialBalance(request.initialBalance());
        return AccountResponse.from(account);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Account account = require(id, userId);
        if (transactionRepository.existsByAccountId(account.getId())) {
            throw new BusinessException("Account has transactions; archive it instead of deleting");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public AccountResponse setArchived(Long id, Long userId, boolean archived) {
        Account account = require(id, userId);
        account.setArchived(archived);
        return AccountResponse.from(account);
    }

    private Account require(Long id, Long userId) {
        return accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + id));
    }
}
