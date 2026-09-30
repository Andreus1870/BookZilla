package com.bookzilla.auth.application.port.out;

import java.util.UUID;

public interface IdentityProvider {

    UUID createUser(
            String firstName,
            String lastName,
            String email,
            String password
    );
}
