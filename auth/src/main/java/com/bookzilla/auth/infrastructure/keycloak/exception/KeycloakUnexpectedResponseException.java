package com.bookzilla.auth.infrastructure.keycloak.exception;

public class KeycloakUnexpectedResponseException extends RuntimeException {
    public KeycloakUnexpectedResponseException(String message) {
        super(message);
    }

    public KeycloakUnexpectedResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
