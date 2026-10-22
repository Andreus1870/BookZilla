package com.bookzilla.auth.domain.operation;

import java.time.Instant;
import java.util.UUID;

public class RetryableOperation {

    private final UUID id;
    private final OperationType operationType;
    private final String payload;
    private OperationStatus operationStatus;
    private int attemptCount;
    private Instant nextAttemptAt;
    private String lastError;
    private final Instant createdAt;
    private Instant updatedAt;

    public RetryableOperation(UUID id,
                              OperationType operationType,
                              String payload,
                              OperationStatus operationStatus,
                              int attemptCount,
                              Instant nextAttemptAt,
                              String lastError,
                              Instant createdAt,
                              Instant updatedAt) {
        this.id = id;
        this.operationType = operationType;
        this.payload = payload;
        this.operationStatus = operationStatus;
        this.attemptCount = attemptCount;
        this.nextAttemptAt = nextAttemptAt;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getPayload() {
        return payload;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
