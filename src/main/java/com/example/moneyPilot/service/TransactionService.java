package com.example.moneyPilot.service;

import com.example.moneyPilot.api.ApiException;
import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.Transaction;
import com.example.moneyPilot.domain.TransactionType;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.mapper.TransactionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;

    public TransactionService(TransactionMapper transactionMapper, CategoryService categoryService) {
        this.transactionMapper = transactionMapper;
        this.categoryService = categoryService;
    }

    @Transactional
    public Transaction create(User user, UUID categoryId, String type, int amount, String memo, LocalDate date) {
        Category category = validateCategory(user, categoryId);
        TransactionType txType = TransactionType.fromApiValue(type);
        if (category.type() != txType) {
            throw new IllegalArgumentException("category type and transaction type must match");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                user.id(),
                categoryId,
                txType,
                amount,
                memo,
                date,
                null,
                now,
                now);
        transactionMapper.insert(transaction);
        return transaction;
    }

    @Transactional
    public Transaction update(User user, UUID id, UUID categoryId, Integer amount, String memo, LocalDate date) {
        Transaction transaction = findOwnedTransaction(user, id);
        if (categoryId != null) {
            Category category = validateCategory(user, categoryId);
            if (category.type() != transaction.getType()) {
                throw new IllegalArgumentException("category type and transaction type must match");
            }
        }
        transaction.update(categoryId, amount, memo, date, OffsetDateTime.now());
        transactionMapper.update(transaction);
        return transaction;
    }

    @Transactional
    public void delete(User user, UUID id) {
        Transaction transaction = findOwnedTransaction(user, id);
        transactionMapper.deleteById(transaction.getId());
    }

    public TransactionListResult list(User user, int year, int month, UUID categoryId, String type) {
        String normalizedType = type == null ? null : TransactionType.fromApiValue(type).name();

        List<Transaction> filtered = transactionMapper.listByCondition(user.id(), year, month, categoryId, normalizedType);

        int totalIncome = filtered.stream()
                .filter(item -> item.getType() == TransactionType.INCOME)
                .mapToInt(Transaction::getAmount)
                .sum();
        int totalExpense = filtered.stream()
                .filter(item -> item.getType() == TransactionType.EXPENSE)
                .mapToInt(Transaction::getAmount)
                .sum();

        return new TransactionListResult(filtered, totalIncome, totalExpense, totalIncome - totalExpense);
    }

    public record TransactionListResult(List<Transaction> items, int totalIncome, int totalExpense, int balance) {}

    private Transaction findOwnedTransaction(User user, UUID id) {
        Transaction transaction = transactionMapper.findById(id);
        if (transaction == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "リソースが見つかりません");
        }
        if (!transaction.getUserId().equals(user.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "このリソースへのアクセス権限がありません");
        }
        return transaction;
    }

    private Category validateCategory(User user, UUID categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null || (!category.system() && !user.id().equals(category.userId()))) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "リソースが見つかりません");
        }
        return category;
    }
}
