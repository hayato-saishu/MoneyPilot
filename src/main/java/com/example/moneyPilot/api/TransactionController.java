package com.example.moneyPilot.api;

import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.Transaction;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.service.AuthService;
import com.example.moneyPilot.service.CategoryService;
import com.example.moneyPilot.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final AuthService authService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public TransactionController(AuthService authService, TransactionService transactionService,
            CategoryService categoryService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ListResponse list(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "category_id", required = false) UUID categoryId,
            @RequestParam(value = "type", required = false) String type) {
                User user = authService.getCurrentUser();
        TransactionService.TransactionListResult result = transactionService.list(user, year, month, categoryId, type);
        List<TransactionResponse> data = result.items().stream().map(this::toResponse).toList();
        Summary summary = new Summary(result.totalIncome(), result.totalExpense(), result.balance());
        return new ListResponse(data, summary);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        public TransactionResponse create(@RequestBody CreateRequest request) {
                User user = authService.getCurrentUser();
        Transaction transaction = transactionService.create(
                user,
                UUID.fromString(request.category_id()),
                request.type(),
                request.amount(),
                request.memo(),
                LocalDate.parse(request.date()));
        return toResponse(transaction);
    }

    @PutMapping("/{id}")
        public TransactionResponse update(@PathVariable("id") UUID id,
            @RequestBody UpdateRequest request) {
                User user = authService.getCurrentUser();
        Transaction transaction = transactionService.update(
                user,
                id,
                request.category_id() == null ? null : UUID.fromString(request.category_id()),
                request.amount(),
                request.memo(),
                request.date() == null ? null : LocalDate.parse(request.date()));
        return toResponse(transaction);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable("id") UUID id) {
                User user = authService.getCurrentUser();
        transactionService.delete(user, id);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        Category category = categoryService.getById(transaction.getCategoryId());
        CategoryResponse categoryResponse = new CategoryResponse(
                category.id().toString(),
                category.name(),
                category.type().toApiValue(),
                category.color(),
                category.system());
        return new TransactionResponse(
                transaction.getId().toString(),
                transaction.getType().toApiValue(),
                transaction.getAmount(),
                transaction.getMemo(),
                transaction.getDate().toString(),
                categoryResponse,
                transaction.getRecurringIncomeId() == null ? null : transaction.getRecurringIncomeId().toString(),
                transaction.getCreatedAt().toString(),
                transaction.getUpdatedAt().toString());
    }

    public record CreateRequest(String category_id, String type, int amount, String memo, String date) {}

    public record UpdateRequest(String category_id, Integer amount, String memo, String date) {}

    public record ListResponse(List<TransactionResponse> data, Summary summary) {}

    public record Summary(int total_income, int total_expense, int balance) {}

    public record CategoryResponse(String id, String name, String type, String color, boolean is_system) {}

    public record TransactionResponse(
            String id,
            String type,
            int amount,
            String memo,
            String date,
            CategoryResponse category,
            String recurring_income_id,
            String created_at,
            String updated_at) {}
}
