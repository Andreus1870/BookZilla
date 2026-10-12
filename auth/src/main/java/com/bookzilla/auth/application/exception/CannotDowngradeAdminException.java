package com.bookzilla.auth.application.exception;

public class CannotDowngradeAdminException extends RuntimeException {
    public CannotDowngradeAdminException(String message) {
        super(message);
    }
}
