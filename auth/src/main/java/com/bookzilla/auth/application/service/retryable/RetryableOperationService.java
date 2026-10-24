package com.bookzilla.auth.application.service.retryable;

import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import org.springframework.stereotype.Service;

@Service
public class RetryableOperationService {

    private final RetryableOperationRepository retryableOperationRepository;

    public RetryableOperationService(RetryableOperationRepository retryableOperationRepository) {
        this.retryableOperationRepository = retryableOperationRepository;
    }

    public void saveRetryableOperation(RetryableOperation retryableOperation) {

        retryableOperationRepository.save(retryableOperation);
    }

}
