CREATE TABLE retryable_operations (
    id UUID PRIMARY KEY,
    operation_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_retryable_operations_status
                                  CHECK ( status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'DEAD') ),

    CONSTRAINT check_retryable_operations_attempt_count
                                  CHECK ( attempt_count >= 0 )
);

CREATE INDEX idx_retryable_operations_pending
    ON retryable_operations (next_attempt_at)
    WHERE status = 'PENDING';
