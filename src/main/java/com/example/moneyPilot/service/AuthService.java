package com.example.moneyPilot.service;

import com.example.moneyPilot.api.ApiException;
import com.example.moneyPilot.mapper.SessionMapper;
import com.example.moneyPilot.mapper.UserMapper;
import com.example.moneyPilot.domain.SessionToken;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, SessionMapper sessionMapper, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userMapper = userMapper;
        this.sessionMapper = sessionMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(String name, String email, String password) {
        User existing = userMapper.findByEmail(email.toLowerCase());
        if (existing != null) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "このメールアドレスはすでに登録されています");
        }

        OffsetDateTime now = OffsetDateTime.now();
        User user = new User(
                UUID.randomUUID(),
                name,
                email.toLowerCase(),
                passwordEncoder.encode(password),
                true,
                now,
                now);
        userMapper.insert(user);
        return user;
    }

    @Transactional
    public Map<String, String> login(String email, String password) {
        User user = userMapper.findByEmail(email.toLowerCase());
        if (user == null || !passwordEncoder.matches(password, user.password())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "認証が必要です");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        sessionMapper.insert(new SessionToken(
                UUID.randomUUID(),
                refreshToken,
                user.id(),
                OffsetDateTime.now().plusDays(30),
                OffsetDateTime.now()));

        return Map.of("access_token", accessToken, "refresh_token", refreshToken);
    }

    public Map<String, String> refresh(String refreshToken) {
        SessionToken token = sessionMapper.findByRefreshToken(refreshToken);
        if (token == null || token.expiresAt().isBefore(OffsetDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "認証が必要です");
        }
        User user = userMapper.findById(token.userId());
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "認証が必要です");
        }
        return Map.of("access_token", jwtService.generateAccessToken(user));
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            sessionMapper.deleteByRefreshToken(refreshToken);
            return;
        }
        User currentUser = getCurrentUser();
        sessionMapper.deleteByUserId(currentUser.id());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID userId)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "認証が必要です");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "認証が必要です");
        }
        return user;
    }
}
