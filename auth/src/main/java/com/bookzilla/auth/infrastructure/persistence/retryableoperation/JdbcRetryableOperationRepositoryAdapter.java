package com.bookzilla.auth.infrastructure.persistence.retryableoperation;

import com.bookzilla.auth.application.port.out.RetryableOperationRepository;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class JdbcRetryableOperationRepositoryAdapter implements RetryableOperationRepository {

    private final JdbcClient jdbcClient;

    public JdbcRetryableOperationRepositoryAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public RetryableOperation save(RetryableOperation operation) {
        jdbcClient.sql("""
            INSERT INTO retryable_operations (
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
        FROM retryable_operations
        WHERE status = 'PENDING'
            AND next_attempt_at <= :now
        ORDER BY next_attempt_at, created_at
        LIMIT :limit
        """)
            .param("now", now)
            .param("limit", limit)
            .query(new RetryableOperationRowMapper())
            .list();

    }

}
