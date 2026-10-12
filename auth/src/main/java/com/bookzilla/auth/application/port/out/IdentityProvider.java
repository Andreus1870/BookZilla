package com.bookzilla.auth.application.port.out;

import java.lang.reflect.MalformedParameterizedTypeException;
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

    boolean hasUserRole(String email);

    boolean hasModeratorRole(String email);

    boolean hasAdminRole(String email);

    void downgradeModerator(String email);

    void promoteToModerator(String email);

    void deleteKeycloakUserRepresentation(String email);

    Map<String, String> getUserRolesByEmails(List<String> emails);
}
