package com.example.moneyPilot.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RecurringIncome {
    private final UUID id;
    private final UUID userId;
    private UUID categoryId;
    private int amount;
    private String memo;
    private int dayOfMonth;
    private boolean active;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public RecurringIncome(UUID id, UUID userId, UUID categoryId, int amount, String memo, int dayOfMonth,
            boolean active, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.memo = memo;
        this.dayOfMonth = dayOfMonth;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public int getAmount() { return amount; }
    public String getMemo() { return memo; }
    public int getDayOfMonth() { return dayOfMonth; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

        public void update(UUID categoryId, Integer amount, String memo, Integer dayOfMonth, Boolean active,
            OffsetDateTime updatedAt) {
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (dayOfMonth != null) {
            this.dayOfMonth = dayOfMonth;
        }
        if (active != null) {
            this.active = active;
        }
        this.updatedAt = updatedAt;
    }
}
