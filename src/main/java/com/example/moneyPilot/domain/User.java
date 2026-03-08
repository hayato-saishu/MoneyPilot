package com.example.moneyPilot.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String email,
        String password,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
