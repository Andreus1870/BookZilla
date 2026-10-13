package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.*;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.BusinessRole;
import com.bookzilla.auth.domain.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRoleService {

    private final IdentityProvider identityProvider;
    private final UserRepository userRepository;

    public UserRoleService(IdentityProvider identityProvider, UserRepository userRepository) {
        this.identityProvider = identityProvider;
        this.userRepository = userRepository;
    }

    public void downgradeModerator(String email) {

        UUID keycloakId = getKeycloakIdByEmail(email);

        String role = identityProvider.getUserRoleByKeycloakId(keycloakId);

        if (role.equals(BusinessRole.USER.name())) {
            throw new CannotDowngradeUserException("Cannot downgrade user with id: " + keycloakId);
        }

        if (role.equals(BusinessRole.ADMIN.name())) {
            throw new CannotDowngradeAdminException("Cannot downgrade admin with id: " + keycloakId);
        }



        identityProvider.downgradeModerator(keycloakId);

    }

    public void promoteToModerator(String email) {

        UUID keycloakId = getKeycloakIdByEmail(email);

        String role = identityProvider.getUserRoleByKeycloakId(keycloakId);

        if (role.equals(BusinessRole.MODERATOR.name())) {
            throw new AlreadyIsModeratorException("Cannot promote moderator with id: " + keycloakId);
        }

        if (role.equals(BusinessRole.ADMIN.name())) {
            throw new CannotPromoteAdminException("Cannot promote admin with id: " + keycloakId);
        }

        identityProvider.promoteToModerator(keycloakId);
    }

    private UUID getKeycloakIdByEmail(String email) {

        return userRepository.getUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException(
                        "User not found with email" + email
                )
        ).getKeycloakId();

    }
}
