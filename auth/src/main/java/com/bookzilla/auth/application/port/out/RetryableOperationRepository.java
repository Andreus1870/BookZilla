package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.operation.RetryableOperation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RetryableOperationRepository {

    RetryableOperation save(RetryableOperation operation);

    List<RetryableOperation> findReadyToRetry(Instant now, int limit);

    Optional<RetryableOperation> findById(UUID id);

    void deleteById(UUID id);

}
