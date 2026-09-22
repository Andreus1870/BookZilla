package com.bookzilla.booking.application.service;

import com.bookzilla.booking.application.port.out.ProviderRepository;
import com.bookzilla.booking.domain.Provider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(transactionManager = "bookingTransactionManager")
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public Provider createProvider(UUID userId) {
        return providerRepository.save(new Provider(userId));
    }
}
