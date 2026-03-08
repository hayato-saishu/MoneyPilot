package com.example.moneyPilot.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID userId;
    private UUID categoryId;
    private TransactionType type;
    private int amount;
    private String memo;
    private LocalDate date;
    private final UUID recurringIncomeId;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Transaction(UUID id, UUID userId, UUID categoryId, TransactionType type, int amount, String memo,
            LocalDate date, UUID recurringIncomeId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.memo = memo;
        this.date = date;
        this.recurringIncomeId = recurringIncomeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public TransactionType getType() { return type; }
    public int getAmount() { return amount; }
    public String getMemo() { return memo; }
    public LocalDate getDate() { return date; }
    public UUID getRecurringIncomeId() { return recurringIncomeId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void update(UUID categoryId, Integer amount, String memo, LocalDate date, OffsetDateTime updatedAt) {
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (date != null) {
            this.date = date;
        }
        this.updatedAt = updatedAt;
    }
}
