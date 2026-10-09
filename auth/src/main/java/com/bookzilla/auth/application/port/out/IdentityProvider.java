package com.bookzilla.auth.application.port.out;

import java.util.UUID;

public interface IdentityProvider {

    UUID createIdentity(
            String firstName,
            String lastName,
            String email,
            String password
    );

    boolean hasUserRole(String email);
}
