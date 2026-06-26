package com.paulouchoa.fintrack.transaction;

import com.paulouchoa.fintrack.common.PageResponse;
import com.paulouchoa.fintrack.common.TransactionType;
import com.paulouchoa.fintrack.security.AppUserDetails;
import com.paulouchoa.fintrack.security.CurrentUser;
import com.paulouchoa.fintrack.transaction.dto.TransactionRequest;
import com.paulouchoa.fintrack.transaction.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Record and query income and expense transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "List transactions with optional filters and pagination")
    public PageResponse<TransactionResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser AppUserDetails user) {
        TransactionFilter filter = new TransactionFilter(from, to, type, accountId, categoryId);
        return transactionService.list(user.getId(), filter, pageable);
    }

    @GetMapping("/{id}")
    public TransactionResponse get(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        return transactionService.get(id, user.getId());
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request,
                                                      @CurrentUser AppUserDetails user) {
        TransactionResponse created = transactionService.create(request, user.getId());
        return ResponseEntity.created(URI.create("/api/transactions/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest request,
                                      @CurrentUser AppUserDetails user) {
        return transactionService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        transactionService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
