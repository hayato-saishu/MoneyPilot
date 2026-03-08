package com.example.moneyPilot.service;

import com.example.moneyPilot.api.ApiException;
import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.RecurringIncome;
import com.example.moneyPilot.domain.Transaction;
import com.example.moneyPilot.domain.TransactionType;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.mapper.RecurringIncomeMapper;
import com.example.moneyPilot.mapper.TransactionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringIncomeService {
    private final RecurringIncomeMapper recurringIncomeMapper;
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;

    public RecurringIncomeService(RecurringIncomeMapper recurringIncomeMapper, TransactionMapper transactionMapper,
            CategoryService categoryService) {
        this.recurringIncomeMapper = recurringIncomeMapper;
        this.transactionMapper = transactionMapper;
        this.categoryService = categoryService;
    }

    public List<RecurringIncome> list(User user) {
        return recurringIncomeMapper.listByUserId(user.id());
    }

    @Transactional
    public RecurringIncome create(User user, UUID categoryId, int amount, String memo, int dayOfMonth) {
        validateDayOfMonth(dayOfMonth);
        validateCategory(user, categoryId);
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        RecurringIncome item = new RecurringIncome(id, user.id(), categoryId, amount, memo, dayOfMonth, true, now, now);
        recurringIncomeMapper.insert(item);
        return item;
    }

    @Transactional
    public RecurringIncome update(User user, UUID id, UUID categoryId, Integer amount, String memo, Integer dayOfMonth,
            Boolean isActive) {
        RecurringIncome item = recurringIncomeMapper.findById(id);
        if (item == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "指定された定期収入設定が見つかりません");
        }
        if (!item.getUserId().equals(user.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "この操作を行う権限がありません");
        }
        if (dayOfMonth != null) {
            validateDayOfMonth(dayOfMonth);
        }
        if (categoryId != null) {
            validateCategory(user, categoryId);
        }
        item.update(categoryId, amount, memo, dayOfMonth, isActive, OffsetDateTime.now());
        recurringIncomeMapper.update(item);
        return item;
    }

    @Transactional
    public void delete(User user, UUID id) {
        RecurringIncome item = recurringIncomeMapper.findById(id);
        if (item == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "リソースが見つかりません");
        }
        if (!item.getUserId().equals(user.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "このリソースへのアクセス権限がありません");
        }
        recurringIncomeMapper.deleteById(id);
    }

    public BatchResult applyRecurringIncomesForDate(LocalDate today) {
        int created = 0;
        int skipped = 0;
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate nextMonthStart = currentMonth.plusMonths(1).atDay(1);

        for (RecurringIncome item : recurringIncomeMapper.listActiveByDayOfMonth(today.getDayOfMonth())) {
            boolean exists = transactionMapper.countByRecurringIncomeInRange(item.getId(), monthStart, nextMonthStart) > 0;
            if (exists) {
                skipped++;
                continue;
            }

            OffsetDateTime now = OffsetDateTime.now();
                transactionMapper.insert(new Transaction(
                    UUID.randomUUID(),
                    item.getUserId(),
                    item.getCategoryId(),
                    TransactionType.INCOME,
                    item.getAmount(),
                    item.getMemo(),
                    today,
                    item.getId(),
                    now,
                    now));
            created++;
        }

        return new BatchResult(created, skipped);
    }

    public record BatchResult(int createdCount, int skippedCount) {}

    private void validateDayOfMonth(int dayOfMonth) {
        if (dayOfMonth < 1 || dayOfMonth > 28) {
            throw new IllegalArgumentException("day_of_month must be between 1 and 28");
        }
    }

    private void validateCategory(User user, UUID categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null || (!category.system() && !user.id().equals(category.userId()))) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "リソースが見つかりません");
        }
    }
}
