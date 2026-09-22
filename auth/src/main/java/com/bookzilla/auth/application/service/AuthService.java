package com.bookzilla.auth.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void login(String email, String password) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

    }
}
