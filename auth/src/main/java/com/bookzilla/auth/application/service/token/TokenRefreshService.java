package com.bookzilla.auth.application.service.token;

import com.bookzilla.auth.application.port.out.token.TokenProvider;
import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshService {

    private final TokenProvider tokenProvider;

    public TokenRefreshService(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public RefreshTokenResponse refreshToken(String refreshToken) {
        return  tokenProvider.refreshToken(refreshToken);
    }
}
