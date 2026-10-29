package com.bookzilla.auth.infrastructure.keycloak.token;

import com.bookzilla.auth.application.dto.RefreshTokenResponse;
import com.bookzilla.auth.application.port.out.token.TokenProvider;
import com.bookzilla.auth.infrastructure.keycloak.exception.InvalidRefreshTokenException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUnavailableException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUnexpectedResponseException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class KeycloakTokenProvider implements TokenProvider {

    private final RestClient restClient;
    private final String tokenUrl;
    private final String clientId;

    @Autowired
    public KeycloakTokenProvider(
            RestClient.Builder restClientBuilder,
            @Value("${keycloak.oauth.token-url}") String tokenUrl,
            @Value("${keycloak.oauth.client-id}") String clientId
    ) {
        this(
                restClientBuilder
                        .requestFactory(createRequestFactory())
                        .build(),
                tokenUrl,
                clientId
        );
    }

    KeycloakTokenProvider(
            RestClient restClient,
            String tokenUrl,
            String clientId
    ) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;

        this.restClient = restClient;
    }

    private static ClientHttpRequestFactory createRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return requestFactory;
    }


    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("refresh_token", refreshToken);

        KeycloakTokenResponse response;

        try {
            response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KeycloakTokenResponse.class);

        } catch (HttpClientErrorException.BadRequest exception) {
            throw translateBadRequest(exception);

        } catch (HttpClientErrorException exception) {
            throw new KeycloakUnexpectedResponseException(
                    "Keycloak token endpoint returned an unexpected client error",
                    exception
            );

        } catch (HttpServerErrorException exception) {
            throw new KeycloakUnexpectedResponseException(
                    "Keycloak token endpoint returned a server error",
                    exception
            );

        } catch (ResourceAccessException exception) {
            throw new KeycloakUnavailableException(
                    "Failed to communicate with Keycloak token endpoint",
                    exception
            );
        }

        if (response == null) {
            throw new KeycloakUnexpectedResponseException(
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

    private RuntimeException translateBadRequest(
            HttpClientErrorException.BadRequest exception
    ) {
        KeycloakErrorResponse error;

        try {
            error = exception.getResponseBodyAs(KeycloakErrorResponse.class);
        } catch (RuntimeException parsingException) {
            return new KeycloakUnexpectedResponseException(
                    "Failed to parse Keycloak error response",
                    parsingException
            );
        }

        if (error != null && "invalid_grant".equals(error.error())) {
            return new InvalidRefreshTokenException();
        }

        return new KeycloakUnexpectedResponseException(
                "Unexpected error from Keycloak token endpoint",
                exception
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

    private record KeycloakErrorResponse(
            String error
    ) {
    }
}
