package com.example.moneyPilot.api;

import com.example.moneyPilot.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.refresh_token());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        authService.logout(refreshToken);
        return Map.of("message", "logged out");
    }

    public record LoginRequest(String email, String password) {}

    public record RefreshRequest(String refresh_token) {}
}
