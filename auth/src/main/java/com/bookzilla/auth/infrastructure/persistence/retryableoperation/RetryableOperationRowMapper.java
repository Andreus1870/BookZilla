package com.bookzilla.auth.infrastructure.persistence.retryableoperation;

import com.bookzilla.auth.domain.operation.OperationStatus;
import com.bookzilla.auth.domain.operation.OperationType;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RetryableOperationRowMapper implements RowMapper<RetryableOperation> {

    @Override
    public RetryableOperation mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new RetryableOperation(
                rs.getObject("id", UUID.class),
                OperationType.valueOf(rs.getObject("operation_type", String.class)),
                rs.getString("payload"),
                OperationStatus.valueOf(rs.getObject("status", String.class)),
                rs.getObject("attempt_count", Integer.class),
                rs.getObject("next_attempt_at", OffsetDateTime.class).toInstant(),
                rs.getObject("last_error", String.class),
                rs.getObject("created_at", OffsetDateTime.class).toInstant(),
                rs.getObject("updated_at", OffsetDateTime.class).toInstant()
        );

    }

}
