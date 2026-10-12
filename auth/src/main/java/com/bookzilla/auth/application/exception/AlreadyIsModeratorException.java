package com.bookzilla.auth.application.exception;

public class AlreadyIsModeratorException extends RuntimeException {
    public AlreadyIsModeratorException(String message) {
        super(message);
    }
}
