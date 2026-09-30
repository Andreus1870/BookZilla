package com.bookzilla.auth.infrastructure.identity;

import com.bookzilla.auth.application.exception.KeycloakUserCreationException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final String keycloakRealm;

    public KeycloakIdentityProvider(Keycloak keycloak,
                                    @Value("${keycloak.realm}") String keycloakRealm) {
        this.keycloak = keycloak;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public UUID createUser(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        UserRepresentation user = new UserRepresentation();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setCredentials(List.of(credential));
        user.setEnabled(true);

        try (Response response = keycloak
                .realm(keycloakRealm)
                .users()
                .create(user)) {

            if (response.getStatus() != 201) {
                throw new KeycloakUserCreationException(
                        "Failed to create Keycloak user: " + response.getStatus()
                );
            }

            String location = response.getHeaderString("Location");

            return UUID.fromString(
                    location.substring(location.lastIndexOf('/') + 1)
            );
        }
    }
}
