package com.bookzilla.auth.infrastructure.keycloak.token;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.infrastructure.keycloak.exception.InvalidRefreshTokenException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUnexpectedResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class KeycloakTokenProviderTest {

    private static final String TOKEN_URL =
            "https://keycloak.example/token";

    private static final String CLIENT_ID = "bookzilla-client";

    private static final String REFRESH_TOKEN = "old-refresh-token";

    private MockRestServiceServer server;
    private KeycloakTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        tokenProvider = new KeycloakTokenProvider(
                builder.build(),
                TOKEN_URL,
                CLIENT_ID
        );
    }

    @Test
    void shouldRefreshToken() {

        // arrange
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(POST))
                .andExpect(header(
                        "Content-Type",
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE
                ))
                .andExpect(content().formDataContains(
                        Map.of("grant_type", "refresh_token")
                ))
                .andExpect(content().formDataContains(
                        Map.of("client_id", CLIENT_ID)
                ))
                .andExpect(content().formDataContains(
                        Map.of("refresh_token", REFRESH_TOKEN)
                ))
                .andRespond(withSuccess("""
                {
                   "access_token": "new-access-token",
                   "refresh_token": "new-refresh-token",
                   "expires_in": 300,
                   "refresh_expires_in": 1800,
                   "token_type": "Bearer"
                }
               """, MediaType.APPLICATION_JSON));

        // act
        RefreshTokenResponse response =
                tokenProvider.refreshToken(REFRESH_TOKEN);

        // assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.expiresIn()).isEqualTo(300);
        assertThat(response.refreshExpiresIn()).isEqualTo(1800);
        assertThat(response.tokenType()).isEqualTo("Bearer");

        server.verify();
    }

    @Test
    void shouldThrowInvalidRefreshTokenExceptionWhenRefreshTokenIsInvalid() {
        // arrange
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                "error": "invalid_grant",
                                "error_description": "Invalid refresh token"
                                }
                                """));

        // act and assert
        assertThatThrownBy(() -> tokenProvider.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void shouldThrowKeycloakUnexpectedResponseExceptionWhenUnexpectedClientErrorOccurs() {
        // arrange
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        // act and assert
        assertThatThrownBy(() -> tokenProvider.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(KeycloakUnexpectedResponseException.class)
                .hasMessage("Keycloak token endpoint returned an unexpected client error");

        server.verify();
    }


    @Test
    void shouldThrowKeycloakUnexpectedResponseExceptionWhenServerErrorOccurs() {
        // arrange
        server.expect(requestTo(TOKEN_URL))
                .andExpect(method(POST))
                .andRespond(withServerError());

        // act and assert
        assertThatThrownBy(() -> tokenProvider.refreshToken(REFRESH_TOKEN))
                .isInstanceOf(KeycloakUnexpectedResponseException.class)
                .hasMessage("Keycloak token endpoint returned a server error");

        server.verify();
    }
}
