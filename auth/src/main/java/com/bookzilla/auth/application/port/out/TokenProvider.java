package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;

public interface TokenProvider {
    RefreshTokenResponse refreshToken(String refreshToken);
}
