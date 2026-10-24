package com.bookzilla.auth.application.port.out.retryable;

import com.bookzilla.auth.domain.operation.RetryableOperation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RetryableOperationRepository {

    RetryableOperation save(RetryableOperation operation);

    List<RetryableOperation> findReadyToRetry(Instant now, int limit);

    boolean markInProgressIfPending(UUID id, Instant now);
}
