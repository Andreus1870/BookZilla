package com.bookzilla.auth.infrastructure.keycloak.exception;

public class KeycloakUserDeletionException extends RuntimeException {

    public KeycloakUserDeletionException(String message) {
        super(message);
    }

    public KeycloakUserDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}