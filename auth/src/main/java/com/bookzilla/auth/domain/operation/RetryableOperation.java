package com.bookzilla.auth.domain.operation;

import java.time.Instant;
import java.util.Objects;
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

    public void markInProgress() {

        if (this.operationStatus == OperationStatus.PENDING) {

            this.operationStatus = OperationStatus.IN_PROGRESS;
            this.updatedAt = Instant.now();
            this.attemptCount++;

        } else {
            throw new IllegalStateException(
                    "Cannot mark operation as IN_PROGRESS because its current status is not PENDING"
            );
        }

    }

    public void markCompleted() {

        if (this.operationStatus == OperationStatus.IN_PROGRESS) {

            this.operationStatus = OperationStatus.COMPLETED;
            this.updatedAt = Instant.now();
            this.lastError = null;

        } else {
            throw new IllegalStateException(
                    "Cannot mark operation as COMPLETED because its current status is not IN_PROGRESS"
            );
        }

    }

    public void scheduleRetry(Instant nextAttemptAt, String lastError) {

        Objects.requireNonNull(nextAttemptAt, "nextAttemptAt cannot be null");
        Objects.requireNonNull(lastError, "lastError cannot be null");


        if (this.operationStatus == OperationStatus.IN_PROGRESS) {

            this.operationStatus = OperationStatus.PENDING;
            this.updatedAt = Instant.now();
            this.nextAttemptAt = nextAttemptAt;
            this.lastError = lastError;

        } else {
            throw new IllegalStateException(
                    "Cannot schedule retry because its current status is not IN_PROGRESS"
            );
        }

    }

    public void markDead() {

        if (this.operationStatus == OperationStatus.PENDING) {

            this.operationStatus = OperationStatus.DEAD;
            this.updatedAt = Instant.now();

        } else {
            throw new IllegalStateException(
                    "Cannot mark dead because its current status is not PENDING"
            );
        }
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
