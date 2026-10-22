package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.*;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.user.BusinessRole;
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

        BusinessRole role = identityProvider.getUserRoleByKeycloakId(keycloakId);

        if (role == BusinessRole.USER) {
            throw new CannotDowngradeUserException("Cannot downgrade user with id: " + keycloakId);
        }

        if (role == BusinessRole.ADMIN) {
            throw new CannotDowngradeAdminException("Cannot downgrade admin with id: " + keycloakId);
        }



        identityProvider.downgradeModerator(keycloakId);

    }

    public void promoteToModerator(String email) {

        UUID keycloakId = getKeycloakIdByEmail(email);

        BusinessRole role = identityProvider.getUserRoleByKeycloakId(keycloakId);

        if (role == BusinessRole.MODERATOR) {
            throw new AlreadyIsModeratorException("Cannot promote moderator with id: " + keycloakId);
        }

        if (role == BusinessRole.ADMIN) {
            throw new CannotPromoteAdminException("Cannot promote admin with id: " + keycloakId);
        }

        identityProvider.promoteToModerator(keycloakId);
    }

    private UUID getKeycloakIdByEmail(String email) {

        return userRepository.getUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException(
                        "User not found with email: " + email
                )
        ).getKeycloakId();

    }
}
