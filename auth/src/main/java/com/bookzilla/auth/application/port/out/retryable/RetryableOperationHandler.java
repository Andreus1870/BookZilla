package com.bookzilla.auth.application.port.out.retryable;

import com.bookzilla.auth.domain.operation.OperationType;

public interface RetryableOperationHandler {

    OperationType getSupportedOperationType();

    void handle(String payload);

}
