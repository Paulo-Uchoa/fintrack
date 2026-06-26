package com.paulouchoa.fintrack.config;

import com.paulouchoa.fintrack.account.Account;
import com.paulouchoa.fintrack.account.AccountRepository;
import com.paulouchoa.fintrack.account.AccountType;
import com.paulouchoa.fintrack.budget.Budget;
import com.paulouchoa.fintrack.budget.BudgetRepository;
import com.paulouchoa.fintrack.category.Category;
import com.paulouchoa.fintrack.category.CategoryRepository;
import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.transaction.Transaction;
import com.paulouchoa.fintrack.transaction.TransactionRepository;
import com.paulouchoa.fintrack.user.Role;
import com.paulouchoa.fintrack.user.User;
import com.paulouchoa.fintrack.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "fintrack.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_EMAIL = "demo@fintrack.app";
    private static final String DEMO_PASSWORD = "demo12345";

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(DEMO_EMAIL)) {
            return;
        }
        log.info("Seeding demo data for {}", DEMO_EMAIL);

        User demo = userRepository.save(new User("Demo User", DEMO_EMAIL,
                passwordEncoder.encode(DEMO_PASSWORD), Role.USER));

        Account checking = accountRepository.save(
                new Account("Checking", AccountType.CHECKING, new BigDecimal("1500.00"), demo));
        Account wallet = accountRepository.save(
                new Account("Wallet", AccountType.WALLET, new BigDecimal("200.00"), demo));

        Category salary = categoryRepository.save(new Category("Salary", TransactionType.INCOME, "#2ecc71", demo));
        Category groceries = categoryRepository.save(new Category("Groceries", TransactionType.EXPENSE, "#e67e22", demo));
        Category rent = categoryRepository.save(new Category("Rent", TransactionType.EXPENSE, "#e74c3c", demo));
        Category leisure = categoryRepository.save(new Category("Leisure", TransactionType.EXPENSE, "#9b59b6", demo));

        YearMonth current = YearMonth.now();
        seedMonth(demo, current, checking, wallet, salary, groceries, rent, leisure);
        seedMonth(demo, current.minusMonths(1), checking, wallet, salary, groceries, rent, leisure);

        budgetRepository.save(new Budget(current.atDay(1), new BigDecimal("600.00"), groceries, demo));
        budgetRepository.save(new Budget(current.atDay(1), new BigDecimal("300.00"), leisure, demo));

        log.info("Demo data ready: login with {} / {}", DEMO_EMAIL, DEMO_PASSWORD);
    }

    private void seedMonth(User user, YearMonth month, Account checking, Account wallet,
                           Category salary, Category groceries, Category rent, Category leisure) {
        LocalDate first = month.atDay(1);
        save("Monthly salary", new BigDecimal("4200.00"), first.plusDays(4), TransactionType.INCOME, checking, salary, user);
        save("Apartment rent", new BigDecimal("1300.00"), first.plusDays(5), TransactionType.EXPENSE, checking, rent, user);
        save("Supermarket", new BigDecimal("210.45"), first.plusDays(8), TransactionType.EXPENSE, checking, groceries, user);
        save("Supermarket", new BigDecimal("178.90"), first.plusDays(19), TransactionType.EXPENSE, checking, groceries, user);
        save("Cinema", new BigDecimal("64.00"), first.plusDays(12), TransactionType.EXPENSE, wallet, leisure, user);
        save("Streaming subscription", new BigDecimal("39.90"), first.plusDays(15), TransactionType.EXPENSE, checking, leisure, user);
    }

    private void save(String description, BigDecimal amount, LocalDate date, TransactionType type,
                      Account account, Category category, User user) {
        transactionRepository.save(new Transaction(description, amount, date, type, account, category, user));
    }
}
