package com.bookzilla.auth.infrastructure.retry.handler;

import com.bookzilla.auth.application.port.out.identity.IdentityProvider;
import com.bookzilla.auth.application.port.out.retryable.RetryableOperationHandler;
import com.bookzilla.auth.domain.operation.OperationType;
import com.bookzilla.auth.domain.operation.payload.DeleteKeycloakUserPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DeleteKeycloakUserHandler implements RetryableOperationHandler {

    private static final OperationType OPERATION_TYPE = OperationType.DELETE_KEYCLOAK_USER;

    private final IdentityProvider identityProvider;
    private final ObjectMapper objectMapper;

    public DeleteKeycloakUserHandler(IdentityProvider identityProvider, ObjectMapper objectMapper) {
        this.identityProvider = identityProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public OperationType getSupportedOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public void handle(String payload) {


        try {

            DeleteKeycloakUserPayload deleteKeycloakUserPayload =
                    objectMapper.readValue(payload, DeleteKeycloakUserPayload.class);

            identityProvider.deleteKeycloakUserRepresentation(
                    deleteKeycloakUserPayload.keycloakId()
            );

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }

    }
}
