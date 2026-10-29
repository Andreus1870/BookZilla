package com.bookzilla.auth.application.port.out.token;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;

public interface TokenProvider {
    RefreshTokenResponse refreshToken(String refreshToken);
}
