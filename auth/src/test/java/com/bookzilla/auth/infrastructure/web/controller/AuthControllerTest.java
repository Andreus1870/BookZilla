package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.application.service.token.TokenRefreshService;
import com.bookzilla.auth.application.service.user.UserService;
import com.bookzilla.auth.infrastructure.web.dto.RefreshTokenRequest;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private TokenRefreshService tokenRefreshService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldDelegateRegistrationToUserService() {
        //arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "user",
                "green",
                "email@example.com",
                "123456789"
        );

        //act
        authController.register(registerRequest);

        //assert
        verify(userService).register(
                "user",
                "green",
                "email@example.com",
                "123456789"
        );
    }

    @Test
    void shouldDelegateTokenRefreshToTokenRefreshService() {

        // arrange
        String refreshToken = "refresh-token";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(
                refreshToken
        );

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(
                "access-token",
                "refresh-token",
                15,
                100,
                "JWT"
        );

        when(tokenRefreshService
                .refreshToken(refreshToken))
                .thenReturn(refreshTokenResponse);


        // act
        RefreshTokenResponse result = authController.refreshToken(refreshTokenRequest);

        // assert
        verify(tokenRefreshService).refreshToken(refreshToken);
        assertThat(result).isSameAs(refreshTokenResponse);
    }
}
