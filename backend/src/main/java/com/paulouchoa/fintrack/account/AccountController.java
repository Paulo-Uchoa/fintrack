package com.paulouchoa.fintrack.account;

import com.paulouchoa.fintrack.account.dto.AccountRequest;
import com.paulouchoa.fintrack.account.dto.AccountResponse;
import com.paulouchoa.fintrack.security.AppUserDetails;
import com.paulouchoa.fintrack.security.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Accounts", description = "Manage the user's financial accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> list(@CurrentUser AppUserDetails user) {
        return accountService.list(user.getId());
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        return accountService.get(id, user.getId());
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request,
                                                  @CurrentUser AppUserDetails user) {
        AccountResponse created = accountService.create(request, user.getId());
        return ResponseEntity.created(URI.create("/api/accounts/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id,
                                  @Valid @RequestBody AccountRequest request,
                                  @CurrentUser AppUserDetails user) {
        return accountService.update(id, request, user.getId());
    }

    @PatchMapping("/{id}/archived")
    public AccountResponse setArchived(@PathVariable Long id,
                                       @RequestParam boolean value,
                                       @CurrentUser AppUserDetails user) {
        return accountService.setArchived(id, user.getId(), value);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        accountService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
