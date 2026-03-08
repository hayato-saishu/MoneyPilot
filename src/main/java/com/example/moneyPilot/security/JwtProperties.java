package com.example.moneyPilot.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        long accessTokenExpirationSeconds,
        long refreshTokenExpirationSeconds,
        String secret
) {
}
