package com.example.moneyPilot.domain;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType fromApiValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("type is required");
        }
        return switch (value.toLowerCase()) {
            case "income" -> INCOME;
            case "expense" -> EXPENSE;
            default -> throw new IllegalArgumentException("type must be income or expense");
        };
    }

    public String toApiValue() {
        return this == INCOME ? "income" : "expense";
    }
}
