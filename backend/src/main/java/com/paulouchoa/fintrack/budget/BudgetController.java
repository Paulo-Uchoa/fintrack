package com.paulouchoa.fintrack.budget;

import com.paulouchoa.fintrack.budget.dto.BudgetRequest;
import com.paulouchoa.fintrack.budget.dto.BudgetResponse;
import com.paulouchoa.fintrack.security.AppUserDetails;
import com.paulouchoa.fintrack.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Budgets", description = "Monthly spending limits per category with live progress")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    @Operation(summary = "List budgets for a month (defaults to the current month)")
    public List<BudgetResponse> list(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @CurrentUser AppUserDetails user) {
        YearMonth target = month != null ? month : YearMonth.now();
        return budgetService.list(user.getId(), target);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody BudgetRequest request,
                                                 @CurrentUser AppUserDetails user) {
        BudgetResponse created = budgetService.create(request, user.getId());
        return ResponseEntity.created(URI.create("/api/budgets/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public BudgetResponse update(@PathVariable Long id,
                                 @Valid @RequestBody BudgetRequest request,
                                 @CurrentUser AppUserDetails user) {
        return budgetService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        budgetService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
