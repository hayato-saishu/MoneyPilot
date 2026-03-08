package com.example.moneyPilot.api;

import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody RegisterRequest request) {
        User user = authService.register(request.name(), request.email(), request.password());
        return new UserResponse(user.id().toString(), user.name(), user.email(), user.createdAt().toString());
    }

    public record RegisterRequest(String name, String email, String password) {}

    public record UserResponse(String id, String name, String email, String created_at) {}
}
