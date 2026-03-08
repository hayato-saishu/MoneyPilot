package com.example.moneyPilot.domain;

import java.util.UUID;
import java.time.OffsetDateTime;

public record Category(
        UUID id,
        UUID userId,
        String name,
        TransactionType type,
        String color,
        boolean system,
        OffsetDateTime createdAt
) {
}
