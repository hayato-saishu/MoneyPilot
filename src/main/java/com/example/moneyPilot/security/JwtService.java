package com.example.moneyPilot.security;

import com.example.moneyPilot.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return generateToken(user, properties.accessTokenExpirationSeconds(), "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, properties.refreshTokenExpirationSeconds(), "refresh");
    }

    public UUID parseAccessTokenUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!"access".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("invalid token type");
        }
        return UUID.fromString(claims.getSubject());
    }

    private String generateToken(User user, long expirationSeconds, String tokenType) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.id().toString())
                .issuer(properties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .claim("type", tokenType)
                .claim("email", user.email())
                .signWith(key)
                .compact();
    }
}
