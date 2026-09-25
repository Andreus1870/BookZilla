package com.bookzilla.auth.application.service;

import com.bookzilla.auth.domain.Role;
import com.bookzilla.auth.infrastructure.security.BookZillaUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final AccessTokenService accessTokenService;

    public AuthService(AuthenticationManager authenticationManager, AccessTokenService accessTokenService) {
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
    }

    public String login(String email, String password) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        BookZillaUserDetails userDetails = (BookZillaUserDetails) authentication.getPrincipal();
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();

        return accessTokenService.generateAccessToken(
                userDetails.getUserId(),
                Role.fromAuthority(authority)
        );
    }
}
