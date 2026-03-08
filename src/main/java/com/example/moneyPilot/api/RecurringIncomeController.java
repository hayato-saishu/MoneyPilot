package com.example.moneyPilot.api;

import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.RecurringIncome;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.service.AuthService;
import com.example.moneyPilot.service.CategoryService;
import com.example.moneyPilot.service.RecurringIncomeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recurring-incomes")
public class RecurringIncomeController {
    private final AuthService authService;
    private final RecurringIncomeService recurringIncomeService;
    private final CategoryService categoryService;

    public RecurringIncomeController(AuthService authService, RecurringIncomeService recurringIncomeService,
            CategoryService categoryService) {
        this.authService = authService;
        this.recurringIncomeService = recurringIncomeService;
        this.categoryService = categoryService;
    }

    @GetMapping
        public List<RecurringIncomeResponse> list() {
                User user = authService.getCurrentUser();
        return recurringIncomeService.list(user).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        public RecurringIncomeResponse create(@RequestBody CreateRequest request) {
                User user = authService.getCurrentUser();
        RecurringIncome item = recurringIncomeService.create(
                user,
                UUID.fromString(request.category_id()),
                request.amount(),
                request.memo(),
                request.day_of_month());
        return toResponse(item);
    }

    @PutMapping("/{id}")
        public RecurringIncomeResponse update(@PathVariable("id") UUID id,
            @RequestBody UpdateRequest request) {
                User user = authService.getCurrentUser();
        RecurringIncome item = recurringIncomeService.update(
                user,
                id,
                request.category_id() == null ? null : UUID.fromString(request.category_id()),
                request.amount(),
                request.memo(),
                request.day_of_month(),
                request.is_active());
        return toResponse(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable("id") UUID id) {
                User user = authService.getCurrentUser();
        recurringIncomeService.delete(user, id);
    }

    private RecurringIncomeResponse toResponse(RecurringIncome item) {
        Category category = categoryService.getById(item.getCategoryId());
        CategoryResponse categoryResponse = new CategoryResponse(
                category.id().toString(),
                category.name(),
                category.type().toApiValue(),
                category.color(),
                category.system());
        return new RecurringIncomeResponse(
                item.getId().toString(),
                categoryResponse,
                item.getAmount(),
                item.getMemo(),
                item.getDayOfMonth(),
                item.isActive(),
                item.getCreatedAt().toString(),
                item.getUpdatedAt().toString());
    }

    public record CreateRequest(String category_id, int amount, String memo, int day_of_month) {}

    public record UpdateRequest(String category_id, Integer amount, String memo, Integer day_of_month,
            Boolean is_active) {}

    public record CategoryResponse(String id, String name, String type, String color, boolean is_system) {}

    public record RecurringIncomeResponse(
            String id,
            CategoryResponse category,
            int amount,
            String memo,
            int day_of_month,
            boolean is_active,
            String created_at,
            String updated_at) {}
}
