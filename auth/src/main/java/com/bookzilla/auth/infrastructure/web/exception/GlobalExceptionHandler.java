package com.bookzilla.auth.infrastructure.web.exception;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.infrastructure.keycloak.exception.InvalidRefreshTokenException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUnavailableException;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUnexpectedResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Void> handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(KeycloakUnexpectedResponseException.class)
    public ResponseEntity<Void> handleKeycloakUnexpectedResponse(
        KeycloakUnexpectedResponseException exception
    ) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Void> handleInvalidRefreshToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(KeycloakUnavailableException.class)
    public ResponseEntity<Void> handleKeycloakUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
