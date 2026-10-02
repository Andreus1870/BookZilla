package com.bookzilla.auth.infrastructure.identity;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakAdminProperties.class)
public class KeycloakConfiguration {

    private final KeycloakAdminProperties keycloakAdminProperties;

    public KeycloakConfiguration(KeycloakAdminProperties keycloakAdminProperties) {
        this.keycloakAdminProperties = keycloakAdminProperties;
    }

    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(keycloakAdminProperties.serverUrl())
                .realm(keycloakAdminProperties.realm())
                .username(keycloakAdminProperties.username())
                .password(keycloakAdminProperties.password())
                .clientId(keycloakAdminProperties.clientId())
                .build();
    }
}
