package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserService;
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

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest){
        userService.register(registerRequest.firstName(),
                            registerRequest.lastName(),
                            registerRequest.email(),
                            registerRequest.password());
    }
}
