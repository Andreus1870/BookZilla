package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.BusinessRole;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IdentityProvider {

    UUID createIdentity(
            String firstName,
            String lastName,
            String email,
            String password
    );

    boolean hasUserRole(UUID keycloakId);

    boolean hasModeratorRole(UUID keycloakId);

    boolean hasAdminRole(UUID keycloakId);

    void downgradeModerator(UUID keycloakId);

    void promoteToModerator(UUID keycloakId);

    void deleteKeycloakUserRepresentation(UUID keycloakId);

    BusinessRole getUserRoleByKeycloakId(UUID keycloakId);

    Map<UUID, String> getUsersRolesByKeycloakIds(List<UUID> keycloakIds);
}
