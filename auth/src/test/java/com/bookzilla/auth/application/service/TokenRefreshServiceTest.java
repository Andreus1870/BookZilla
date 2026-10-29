package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.application.port.out.token.TokenProvider;
import com.bookzilla.auth.application.service.token.TokenRefreshService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenRefreshServiceTest {

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    @Test
    void shouldDelegateTokenRefreshToTokenProvider() {

        // arrange
        String refreshToken = "refresh-token";

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                "access-token",
                "refresh-token",
                15,
                1000,
                "JWT"
        );

        when(tokenProvider.refreshToken(refreshToken)).thenReturn(refreshTokenResponse);

        // act
        RefreshTokenResponse result = tokenRefreshService.refreshToken(refreshToken);

        // assert
        assertThat(refreshTokenResponse).isSameAs(result);
        verify(tokenProvider).refreshToken(refreshToken);
    }
}
