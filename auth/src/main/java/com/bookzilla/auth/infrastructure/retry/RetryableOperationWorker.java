package com.bookzilla.auth.infrastructure.retry;

import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class RetryableOperationWorker {

    RetryableOperationRepository retryableOperationRepository;

    public RetryableOperationWorker(RetryableOperationRepository retryableOperationRepository) {
        this.retryableOperationRepository = retryableOperationRepository;
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
                operation -> {
                    // delegate to RetryableOperationExecutor
                }
        );



    }

}
