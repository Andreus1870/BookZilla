package com.bookzilla.auth.infrastructure.web;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest){
        userService.register(registerRequest.firstName(),
                            registerRequest.lastName(),
                            registerRequest.password(),
                            registerRequest.email());
    }
}
