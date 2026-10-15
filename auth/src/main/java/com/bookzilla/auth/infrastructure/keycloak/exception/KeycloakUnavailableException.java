package com.bookzilla.auth.infrastructure.keycloak.exception;

public class KeycloakUnavailableException extends RuntimeException {
    public KeycloakUnavailableException(String message) {
        super(message);

    }

    public KeycloakUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
