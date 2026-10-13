package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.AlreadyIsModeratorException;
import com.bookzilla.auth.application.exception.CannotDowngradeAdminException;
import com.bookzilla.auth.application.exception.CannotDowngradeUserException;
import com.bookzilla.auth.application.exception.CannotPromoteAdminException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final IdentityProvider identityProvider;

    public UserRoleService(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void downgradeModerator(String email) {

        if (!identityProvider.hasModeratorRole(email)) {
            if (identityProvider.hasUserRole(email)) {
                throw new CannotDowngradeUserException(
                        "Cannot downgrade user with email: " + email
                );
            }

            throw new CannotDowngradeAdminException(
                    "Cannot downgrade admin with email: " + email
            );
        }


        identityProvider.downgradeModerator(email);

    }

    public void promoteToModerator(String email) {

        if (identityProvider.hasModeratorRole(email)) {
            throw new AlreadyIsModeratorException("Cannot promote moderator with email: " + email);
        }

        if (identityProvider.hasAdminRole(email)) {
            throw new CannotPromoteAdminException("Cannot promote admin with email: " + email);
        }

        identityProvider.promoteToModerator(email);
    }
}
