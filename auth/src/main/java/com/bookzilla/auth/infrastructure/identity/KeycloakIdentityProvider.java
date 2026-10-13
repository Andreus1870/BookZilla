package com.bookzilla.auth.infrastructure.identity;

import com.bookzilla.auth.application.exception.KeycloakUserCreationException;
import com.bookzilla.auth.application.exception.KeycloakUserDeletionException;
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

import java.util.*;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {


    private final Keycloak keycloak;
    private final String keycloakRealm;

    private static final String USER_ROLE = "USER";
    private static final String MODERATOR_ROLE = "MODERATOR";
    private static final String ADMIN_ROLE = "ADMIN";

    private static final Set<String> BUSINESS_ROLES =
            Set.of(USER_ROLE, MODERATOR_ROLE, ADMIN_ROLE);

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
    public boolean hasUserRole(UUID keycloakId) {

        return hasRole(keycloakId, USER_ROLE);
    }

    @Override
    public boolean hasModeratorRole(UUID keycloakId) {

        return hasRole(keycloakId, MODERATOR_ROLE);
    }

    @Override
    public boolean hasAdminRole(UUID keycloakId) {

        return hasRole(keycloakId, ADMIN_ROLE);
    }

    @Override
    public void promoteToModerator(UUID keycloakId) {

        changeRole(keycloakId, USER_ROLE, MODERATOR_ROLE);
    }

    @Override
    public void downgradeModerator(UUID keycloakId) {

        changeRole(keycloakId, MODERATOR_ROLE, USER_ROLE);
    }

    @Override
    public void deleteKeycloakUserRepresentation(UUID keycloakId) {

        try (Response response = keycloak.realm(keycloakRealm)
                .users()
                .delete(keycloakId.toString())) {

            if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new KeycloakUserDeletionException(
                        "Failed to delete Keycloak user. HTTP status: "
                                + response.getStatus()
                );
            }
        }

    }

    @Override
    public Map<UUID, String> getUsersRolesByKeycloakIds(List<UUID> keycloakIds) {

        Map<UUID, String> rolesByKeycloakIds = new HashMap<>();

        keycloakIds.forEach(id -> {
            String role = getUserRoleByKeycloakId(id);

            rolesByKeycloakIds.put(id, role);
        });

        return rolesByKeycloakIds;
    }

    @Override
    public String getUserRoleByKeycloakId(UUID keycloakId) {

        return getUserRoles(keycloakId).stream()
                .map(RoleRepresentation::getName)
                .filter(BUSINESS_ROLES::contains)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Business role was not found for user with id: " + keycloakId)
                );

    }







    private List<RoleRepresentation> getUserRoles(UUID keycloakId) {

        return keycloak.realm(keycloakRealm)
                .users()
                .get(keycloakId.toString())
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

    private boolean hasRole(UUID keycloakId, String roleName) {

        return getUserRoles(keycloakId)
                .stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private void changeRole(UUID keycloakId, String oldRole, String newRole) {

        RoleMappingResource roleMapping = keycloak.realm(keycloakRealm)
                .users()
                .get(keycloakId.toString())
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
