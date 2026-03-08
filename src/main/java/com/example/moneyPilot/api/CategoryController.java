package com.example.moneyPilot.api;

import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.service.AuthService;
import com.example.moneyPilot.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final AuthService authService;
    private final CategoryService categoryService;

    public CategoryController(AuthService authService, CategoryService categoryService) {
        this.authService = authService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> list(
            @RequestParam(value = "type", required = false) String type) {
        User user = authService.getCurrentUser();
        return categoryService.list(user, type).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody CreateCategoryRequest request) {
        User user = authService.getCurrentUser();
        Category category = categoryService.create(user, request.name(), request.type(), request.color());
        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.id().toString(),
                category.name(),
                category.type().toApiValue(),
                category.color(),
                category.system());
    }

    public record CreateCategoryRequest(String name, String type, String color) {}

    public record CategoryResponse(String id, String name, String type, String color, boolean is_system) {}
}
