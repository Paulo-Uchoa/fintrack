package com.paulouchoa.fintrack.category;

import com.paulouchoa.fintrack.category.dto.CategoryRequest;
import com.paulouchoa.fintrack.category.dto.CategoryResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories", description = "Manage income and expense categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> list(@CurrentUser AppUserDetails user) {
        return categoryService.list(user.getId());
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        return categoryService.get(id, user.getId());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request,
                                                   @CurrentUser AppUserDetails user) {
        CategoryResponse created = categoryService.create(request, user.getId());
        return ResponseEntity.created(URI.create("/api/categories/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CategoryRequest request,
                                   @CurrentUser AppUserDetails user) {
        return categoryService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @CurrentUser AppUserDetails user) {
        categoryService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
