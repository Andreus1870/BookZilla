package com.bookzilla.auth.infrastructure.identity;

import com.bookzilla.auth.application.exception.KeycloakUserCreationException;
import com.bookzilla.auth.application.exception.KeycloakUserDeletionException;
import com.bookzilla.auth.application.exception.KeycloakUserRepresentationNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {

    private static final String USER_ROLE = "USER";

    private final Keycloak keycloak;
    private final String keycloakRealm;


    public KeycloakIdentityProvider(Keycloak keycloak,
                                            @Value("${keycloak.realm}") String keycloakRealm) {
        this.keycloak = keycloak;
        this.keycloakRealm = keycloakRealm;
    }


    @Override
    public UUID createIdentity(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        UserRepresentation user =
                createUserRepresentation(firstName, lastName, email, password);

        UUID userId = createKeycloakUser(user);

        assignUserRole(userId);

        return userId;
    }


    private UUID createKeycloakUser(UserRepresentation user) {
        try (Response response = keycloak
                .realm(keycloakRealm)
                .users()
                .create(user)) {

            if (response.getStatus() != 201) {
                throw new KeycloakUserCreationException(
                        "Failed to create Keycloak user: " + response.getStatus()
                );
            }

            String createdId = CreatedResponseUtil.getCreatedId(response);

            if (createdId == null) {
                throw new KeycloakUserCreationException(
                        "Keycloak did not return an ID for the created user"
                );
            }

            return UUID.fromString(createdId);

        }
    }


    private void assignUserRole(UUID keycloakUserId) {
        RoleRepresentation userRole =
                keycloak
                        .realm(keycloakRealm)
                        .roles()
                        .get(USER_ROLE)
                        .toRepresentation();

        keycloak
                .realm(keycloakRealm)
                .users()
                .get(keycloakUserId.toString())
                .roles()
                .realmLevel()
                .add(List.of(userRole));
    }


    private static UserRepresentation createUserRepresentation(
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

        return user;
    }


    @Override
    public boolean hasUserRole(String email) {

        UserRepresentation user = keycloak.realm(keycloakRealm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                    new KeycloakUserRepresentationNotFoundException(
                            "Keycloak user not found by email: " + email
                    )
                );

        List<RoleRepresentation> roles = keycloak.realm(keycloakRealm)
                .users()
                .get(user.getId())
                .roles()
                .realmLevel()
                .listAll();

        return roles.stream()
                .anyMatch(role -> role.getName().equals("USER"));

    }


    @Override
    public void deleteKeycloakUserRepresentation(String email) {

        UserRepresentation user = keycloak.realm(keycloakRealm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new KeycloakUserRepresentationNotFoundException(
                                "Keycloak user not found by email: " + email
                        )
                );


        try (Response response = keycloak.realm(keycloakRealm)
                .users()
                .delete(user.getId())) {

            if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new KeycloakUserDeletionException(
                        "Failed to delete Keycloak user. HTTP status: "
                                + response.getStatus()
                );
            }
        }

    }
}
