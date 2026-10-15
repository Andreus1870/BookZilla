package com.bookzilla.auth.infrastructure.keycloak.exception;

public class KeycloakUserRepresentationNotFoundException extends RuntimeException {
    public KeycloakUserRepresentationNotFoundException(String message) {
        super(message);
    }
}
