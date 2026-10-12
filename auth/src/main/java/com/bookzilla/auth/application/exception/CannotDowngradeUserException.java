package com.bookzilla.auth.application.exception;

public class CannotDowngradeUserException extends RuntimeException {
    public CannotDowngradeUserException(String message) {
        super(message);
    }
}
