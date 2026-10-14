package com.bookzilla.auth.infrastructure.web.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        Integer refreshExpiresIn,
        String tokenType
) {
}
