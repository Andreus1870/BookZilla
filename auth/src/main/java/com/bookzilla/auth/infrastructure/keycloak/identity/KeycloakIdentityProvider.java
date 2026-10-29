package com.bookzilla.auth.infrastructure.keycloak.identity;

import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUserCreationException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUserDeletionException;
import com.bookzilla.auth.application.port.out.identity.IdentityProvider;
import com.bookzilla.auth.domain.user.BusinessRole;
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
import java.util.stream.Collectors;

@Component
public class KeycloakIdentityProvider implements IdentityProvider {


    private final Keycloak keycloak;
    private final String keycloakRealm;


    private static final Set<String> BUSINESS_ROLES =
            Set.of(BusinessRole.USER.name(),
                    BusinessRole.MODERATOR.name(),
                    BusinessRole.ADMIN.name());

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

        return hasRole(keycloakId, BusinessRole.USER);
    }

    @Override
    public boolean hasModeratorRole(UUID keycloakId) {

        return hasRole(keycloakId, BusinessRole.MODERATOR);
    }

    @Override
    public boolean hasAdminRole(UUID keycloakId) {

        return hasRole(keycloakId, BusinessRole.ADMIN);
    }

    @Override
    public void promoteToModerator(UUID keycloakId) {

        changeRole(keycloakId, BusinessRole.USER, BusinessRole.MODERATOR);
    }

    @Override
    public void downgradeModerator(UUID keycloakId) {

        changeRole(keycloakId, BusinessRole.MODERATOR, BusinessRole.USER);
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
    public Map<UUID, BusinessRole> getUsersRolesByKeycloakIds(List<UUID> keycloakIds) {

        return keycloakIds.stream()
                .collect(
                        Collectors.toMap(id -> id, this::getUserRoleByKeycloakId)
                );
    }


    @Override
    public BusinessRole getUserRoleByKeycloakId(UUID keycloakId) {

        return getUserRoles(keycloakId).stream()
                .map(RoleRepresentation::getName)
                .filter(BUSINESS_ROLES::contains)
                .map(BusinessRole::valueOf)
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
                        .get(BusinessRole.USER.name())
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

    private boolean hasRole(UUID keycloakId, BusinessRole roleName) {

        return getUserRoles(keycloakId)
                .stream()
                .anyMatch(role ->
                        roleName.name().equals(role.getName()));
    }

    private void changeRole(UUID keycloakId, BusinessRole oldRole, BusinessRole newRole) {

        RoleMappingResource roleMapping = keycloak.realm(keycloakRealm)
                .users()
                .get(keycloakId.toString())
                .roles();

        RoleRepresentation oldRoleRepresentation = keycloak.realm(keycloakRealm)
                .roles()
                .get(oldRole.name())
                .toRepresentation();

        RoleRepresentation newRoleRepresentation = keycloak.realm(keycloakRealm)
                .roles()
                .get(newRole.name())
                .toRepresentation();

        roleMapping.realmLevel().remove(List.of(oldRoleRepresentation));
        roleMapping.realmLevel().add(List.of(newRoleRepresentation));
    }
}
