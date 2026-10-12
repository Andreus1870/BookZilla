package com.bookzilla.auth.application.exception;

public class CannotPromoteAdminException extends RuntimeException {
    public CannotPromoteAdminException(String message) {
        super(message);
    }
}
