package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.application.service.TokenRefreshService;
import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.RefreshTokenRequest;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenRefreshService tokenRefreshService;

    public AuthController(UserService userService, TokenRefreshService tokenRefreshService) {
        this.userService = userService;
        this.tokenRefreshService = tokenRefreshService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest){

        userService.register(registerRequest.firstName(),
                            registerRequest.lastName(),
                            registerRequest.email(),
                            registerRequest.password());
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        return tokenRefreshService.refreshToken(refreshTokenRequest.refreshToken());
    }
}
