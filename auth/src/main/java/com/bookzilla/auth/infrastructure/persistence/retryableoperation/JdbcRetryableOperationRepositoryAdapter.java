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
        return null;
    }

    @Override
    public List<RetryableOperation> findReadyToRetry(Instant now, int limit) {
        return List.of();
    }
}
