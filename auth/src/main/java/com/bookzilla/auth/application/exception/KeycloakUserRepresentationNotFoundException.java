package com.bookzilla.auth.application.exception;

public class KeycloakUserRepresentationNotFoundException extends RuntimeException {
    public KeycloakUserRepresentationNotFoundException(String message) {
        super(message);
    }
}
