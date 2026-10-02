package com.bookzilla.auth.infrastructure.identity;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.admin")
public record KeycloakAdminProperties(
        String serverUrl,
        String realm,
        String username,
        String password,
        String clientId
) {
}
