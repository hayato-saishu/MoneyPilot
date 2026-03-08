package com.example.moneyPilot.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionToken(
        UUID id,
        String refreshToken,
        UUID userId,
        OffsetDateTime expiresAt,
        OffsetDateTime createdAt
) {
}
