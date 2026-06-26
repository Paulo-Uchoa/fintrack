package com.paulouchoa.fintrack.report;

import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.report.dto.CategorySummary;
import com.paulouchoa.fintrack.report.dto.MonthlySummaryResponse;
import com.paulouchoa.fintrack.transaction.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlySummaryResponse monthlySummary(Long userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumByType(userId, TransactionType.INCOME, start, end);
        BigDecimal totalExpense = transactionRepository.sumByType(userId, TransactionType.EXPENSE, start, end);

        return new MonthlySummaryResponse(
                month.format(MONTH_FORMAT),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                byCategory(userId, TransactionType.INCOME, start, end),
                byCategory(userId, TransactionType.EXPENSE, start, end));
    }

    @Transactional(readOnly = true)
    public List<CategorySummary> byCategory(Long userId, TransactionType type, LocalDate from, LocalDate to) {
        return transactionRepository.sumByCategory(userId, type, from, to).stream()
                .map(CategorySummary::from)
                .toList();
    }
}
