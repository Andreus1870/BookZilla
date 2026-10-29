package com.bookzilla.auth.application.service.retryable;

import com.bookzilla.auth.application.port.out.retryable.RetryableOperationHandler;
import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.domain.operation.OperationType;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RetryableOperationExecutor {

    private static final int MAX_ATTEMPTS = 10;
    private static final long INITIAL_DELAY_IN_MINUTES = 10;

    private final RetryableOperationRepository retryableOperationRepository;
    private final Map<OperationType, RetryableOperationHandler> retryableOperationHandlers;

    public RetryableOperationExecutor(RetryableOperationRepository retryableOperationRepository,
                                      List<RetryableOperationHandler> retryableOperationHandlers) {

        this.retryableOperationRepository = retryableOperationRepository;

        this.retryableOperationHandlers = retryableOperationHandlers.stream()
                .collect(Collectors.toMap(
                        RetryableOperationHandler::getSupportedOperationType,
                        Function.identity()
                ));
    }


    public void execute(RetryableOperation operation) {

        RetryableOperationHandler operationHandler =
                retryableOperationHandlers.get(operation.getOperationType());

        if (operationHandler == null) {
            throw new IllegalStateException(
                    "No handler registered for operation type: "
                            + operation.getOperationType()
            );
        }


        try {
            operationHandler.handle(operation.getPayload());

        } catch (Exception exception) {

            handleFailure(operation, exception);
            return;
        }

        operation.markCompleted();
        retryableOperationRepository.update(operation);

    }

    private void handleFailure(RetryableOperation operation, Exception exception) {

        String lastError = exception.getMessage() == null
                ? exception.getClass().getName()
                : exception.getMessage();

        if (operation.getAttemptCount() >= MAX_ATTEMPTS) {

            operation.markDead();

        } else {

            long delayMinutes = INITIAL_DELAY_IN_MINUTES
                    * (1L << (operation.getAttemptCount() - 1));

            operation.scheduleRetry(
                    Instant.now().plus(delayMinutes, ChronoUnit.MINUTES),
                    lastError
            );

        }

        retryableOperationRepository.update(operation);

    }
}
