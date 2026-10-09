package com.bookzilla.auth.application.exception;

public class CannotDeleteModeratorException extends RuntimeException {
    public CannotDeleteModeratorException(String message) {
        super(message);
    }
}
