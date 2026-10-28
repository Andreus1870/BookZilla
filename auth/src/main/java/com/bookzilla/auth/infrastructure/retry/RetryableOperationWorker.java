package com.bookzilla.auth.infrastructure.retry;

import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.application.service.retryable.RetryableOperationExecutor;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class RetryableOperationWorker {

    private final RetryableOperationRepository retryableOperationRepository;
    private final RetryableOperationExecutor retryableOperationExecutor;

    public RetryableOperationWorker(RetryableOperationRepository retryableOperationRepository,
                                    RetryableOperationExecutor retryableOperationExecutor) {
        this.retryableOperationRepository = retryableOperationRepository;
        this.retryableOperationExecutor = retryableOperationExecutor;
    }

    @Scheduled(fixedDelayString = "${bookzilla.retry.fixed-delay}")
    public void processRetryableOperations() {


        List<RetryableOperation> retryableOperations =
                retryableOperationRepository.findReadyToRetry(Instant.now(), 5);

        List<RetryableOperation> operationsMarkedInProgress = new ArrayList<>();

        retryableOperations.forEach(
                operation -> {

                    boolean markedInProgress = retryableOperationRepository.markInProgressIfPending(
                            operation.getId(), Instant.now()
                    );

                    if (markedInProgress) {
                        operation.markInProgress();
                        operationsMarkedInProgress.add(operation);
                    }
                }
        );


        operationsMarkedInProgress.forEach(
                retryableOperationExecutor::execute
        );

    }

}
