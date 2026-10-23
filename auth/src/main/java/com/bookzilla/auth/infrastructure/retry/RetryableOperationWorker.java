package com.bookzilla.auth.infrastructure.retry;

import com.bookzilla.auth.domain.operation.RetryableOperation;
import com.bookzilla.auth.infrastructure.persistence.retryableoperation.JdbcRetryableOperationRepositoryAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class RetryableOperationWorker {

    JdbcRetryableOperationRepositoryAdapter jdbcRetryableOperationRepositoryAdapter;

    public RetryableOperationWorker(JdbcRetryableOperationRepositoryAdapter jdbcRetryableOperationRepositoryAdapter) {
        this.jdbcRetryableOperationRepositoryAdapter = jdbcRetryableOperationRepositoryAdapter;
    }

    @Scheduled(fixedDelayString = "${bookzilla.retry.fixed-delay}")
    public void processRetryableOperations() {


        List<RetryableOperation> retryableOperations =
                jdbcRetryableOperationRepositoryAdapter.findReadyToRetry(Instant.now(), 5);

        List<RetryableOperation> operationsMarkedInProgress = new ArrayList<>();

        retryableOperations.forEach(
                operation -> {

                    boolean markedInProgress = jdbcRetryableOperationRepositoryAdapter.markInProgressIfPending(
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
