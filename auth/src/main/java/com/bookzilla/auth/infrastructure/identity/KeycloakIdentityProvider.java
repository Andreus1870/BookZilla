package com.bookzilla.auth.infrastructure.identity;

import com.bookzilla.auth.application.exception.KeycloakUserCreationException;
import com.bookzilla.auth.application.exception.KeycloakUserDeletionException;
import com.bookzilla.auth.application.exception.KeycloakUserRepresentationNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {


    private final Keycloak keycloak;
    private final String keycloakRealm;

    private static final String USER_ROLE = "USER";
    private static final String MODERATOR_ROLE = "MODERATOR";
    private static final String ADMIN_ROLE = "ADMIN";

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

    @Override
    public boolean hasUserRole(String email) {

        return hasRole(email, USER_ROLE);
    }

    @Override
    public boolean hasModeratorRole(String email) {

        return hasRole(email, MODERATOR_ROLE);
    }

    @Override
    public boolean hasAdminRole(String email) {

        return hasRole(email, ADMIN_ROLE);
    }

    @Override
    public void promoteToModerator(String email) {

        changeRole(email, USER_ROLE, MODERATOR_ROLE);
    }

    @Override
    public void downgradeModerator(String email) {

        changeRole(email, MODERATOR_ROLE, USER_ROLE);
    }

    @Override
    public void deleteKeycloakUserRepresentation(String email) {

        UserRepresentation user = getUserRepresentationByEmail(email);


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





    private UserRepresentation getUserRepresentationByEmail(String email) {
        return keycloak.realm(keycloakRealm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new KeycloakUserRepresentationNotFoundException(
                                "Keycloak user not found by email: " + email
                        )
                );
    }

    private List<RoleRepresentation> getUserRoles(String email) {
        UserRepresentation user = getUserRepresentationByEmail(email);

        return keycloak.realm(keycloakRealm)
                .users()
                .get(user.getId())
                .roles()
                .realmLevel()
                .listAll();
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

    private boolean hasRole(String email, String roleName) {

        return getUserRoles(email)
                .stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private void changeRole(String email, String oldRole, String newRole) {
        UserRepresentation user = getUserRepresentationByEmail(email);

        RoleMappingResource roleMapping = keycloak.realm(keycloakRealm)
                .users()
                .get(user.getId())
                .roles();

        RoleRepresentation oldRoleRepresentation = keycloak.realm(keycloakRealm)
                .roles()
                .get(oldRole)
                .toRepresentation();

        RoleRepresentation newRoleRepresentation = keycloak.realm(keycloakRealm)
                .roles()
                .get(newRole)
                .toRepresentation();

        roleMapping.realmLevel().remove(List.of(oldRoleRepresentation));
        roleMapping.realmLevel().add(List.of(newRoleRepresentation));
    }
}
