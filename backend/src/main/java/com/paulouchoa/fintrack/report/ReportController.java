package com.paulouchoa.fintrack.report;

import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.report.dto.CategorySummary;
import com.paulouchoa.fintrack.report.dto.MonthlySummaryResponse;
import com.paulouchoa.fintrack.security.AppUserDetails;
import com.paulouchoa.fintrack.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Aggregated financial reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    @Operation(summary = "Monthly income vs expense summary with per-category breakdown")
    public MonthlySummaryResponse summary(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @CurrentUser AppUserDetails user) {
        YearMonth target = month != null ? month : YearMonth.now();
        return reportService.monthlySummary(user.getId(), target);
    }

    @GetMapping("/by-category")
    @Operation(summary = "Totals grouped by category for a date range and transaction type")
    public List<CategorySummary> byCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam TransactionType type,
            @CurrentUser AppUserDetails user) {
        return reportService.byCategory(user.getId(), type, from, to);
    }
}
