package com.bookzilla.auth.infrastructure.keycloak.token;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.application.port.out.TokenProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KeycloakTokenProvider implements TokenProvider {

    private final RestClient restClient;
    private final String tokenUrl;
    private final String clientId;

    public KeycloakTokenProvider(
            RestClient.Builder restClientBuilder,
            @Value("${keycloak.oauth.token-url}") String tokenUrl,
            @Value("${keycloak.oauth.client-id}") String clientId
    ) {
        this.restClient = restClientBuilder.build();
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("refresh_token", refreshToken);

        KeycloakTokenResponse response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeycloakTokenResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Keycloak returned an empty token response"
            );
        }

        return new RefreshTokenResponse(
                response.accessToken(),
                response.refreshToken(),
                response.expiresIn(),
                response.refreshExpiresIn(),
                response.tokenType()
        );
    }

    private record KeycloakTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") Integer expiresIn,
            @JsonProperty("refresh_expires_in") Integer refreshExpiresIn,
            @JsonProperty("token_type") String tokenType
    ) {
    }
}
