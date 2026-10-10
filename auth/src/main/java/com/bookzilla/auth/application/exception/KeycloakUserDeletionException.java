package com.bookzilla.auth.application.exception;

public class KeycloakUserDeletionException extends RuntimeException {

    public KeycloakUserDeletionException(String message) {
        super(message);
    }

    public KeycloakUserDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}