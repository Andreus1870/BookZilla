package com.bookzilla.auth.infrastructure.persistence.retryableoperation;

import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class JdbcRetryableOperationRepositoryAdapter implements RetryableOperationRepository {

    private final JdbcClient jdbcClient;

    public JdbcRetryableOperationRepositoryAdapter(
            @Qualifier("compensationJdbcClient") JdbcClient jdbcClient
    ) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public RetryableOperation save(RetryableOperation operation) {
        jdbcClient.sql("""
            INSERT INTO auth_retryable_operations (
                id,
                operation_type,
                payload,
                status,
                attempt_count,
                next_attempt_at,
                last_error,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :operationType,
                CAST(:payload AS jsonb),
                :status,
                :attemptCount,
                :nextAttemptAt,
                :lastError,
                :createdAt,
                :updatedAt
            )
            """)
                .param("id", operation.getId())
                .param("operationType", operation.getOperationType().name())
                .param("payload", operation.getPayload())
                .param("status", operation.getOperationStatus().name())
                .param("attemptCount", operation.getAttemptCount())
                .param("nextAttemptAt", operation.getNextAttemptAt())
                .param("lastError", operation.getLastError())
                .param("createdAt", operation.getCreatedAt())
                .param("updatedAt", operation.getUpdatedAt())
                .update();

        return operation;
    }

    @Override
    public List<RetryableOperation> findReadyToRetry(Instant now, int limit) {

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero.");
        }

        return jdbcClient.sql(
        """
        SELECT *
        FROM auth_retryable_operations
        WHERE status = 'PENDING'
            AND next_attempt_at <= :now
        ORDER BY next_attempt_at, created_at
        LIMIT :limit
        """)
            .param("now", now.atOffset(ZoneOffset.UTC))
            .param("limit", limit)
            .query(new RetryableOperationRowMapper())
            .list();

    }

    @Override
    public boolean markInProgressIfPending(UUID id, Instant now) {

        int updatedRows = jdbcClient.sql(
                        """
                        UPDATE auth_retryable_operations
                        SET status = 'IN_PROGRESS',
                            attempt_count = attempt_count + 1,
                            updated_at = :now
                        WHERE id = :id
                            AND status = 'PENDING'
                        """)
                .param("now", now.atOffset(ZoneOffset.UTC))
                .param("id", id)
                .update();

        return updatedRows == 1;
    }

    @Override
    public void update(RetryableOperation retryableOperation) {

        int updatedRows = jdbcClient.sql(
        """
        UPDATE auth_retryable_operations
        SET status = :status,
            updated_at = :updated_at,
            next_attempt_at = :next_attempt_at,
            last_error = :last_error
        WHERE
            id = :id
        """)
        .param("status", retryableOperation.getOperationStatus().name())
        .param("updated_at", retryableOperation.getUpdatedAt())
        .param("next_attempt_at", retryableOperation.getNextAttemptAt())
        .param("last_error", retryableOperation.getLastError())
        .param("id", retryableOperation.getId())
                .update();

        if (updatedRows != 1) {
            throw new IllegalStateException(
                    "Expected to update exactly one retryable operation, but updated "
                            + updatedRows
                            + " rows. Operation ID: "
                            + retryableOperation.getId()
            );
        }
    }
}
