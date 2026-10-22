package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.operation.RetryableOperation;

import java.time.Instant;
import java.util.List;

public interface RetryableOperationRepository {

    RetryableOperation save(RetryableOperation operation);

    List<RetryableOperation> findReadyToRetry(Instant now, int limit);

}
