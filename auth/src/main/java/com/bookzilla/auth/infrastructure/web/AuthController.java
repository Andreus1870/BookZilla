package com.bookzilla.auth.infrastructure.web;

import com.bookzilla.auth.application.service.AuthService;
import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.LoginRequest;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/api/auth/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest){
        userService.register(registerRequest.firstName(),
                            registerRequest.lastName(),
                            registerRequest.password(),
                            registerRequest.email());
    }

    @PostMapping("/api/auth/login")
    public void login(@Valid @RequestBody LoginRequest loginRequest){
        authService.login(loginRequest.email(), loginRequest.password());
    }
}
