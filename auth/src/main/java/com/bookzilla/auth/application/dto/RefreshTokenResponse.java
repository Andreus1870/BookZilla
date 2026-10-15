package com.bookzilla.auth.application.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        Integer refreshExpiresIn,
        String tokenType
) {
}
