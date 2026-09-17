package com.bookzilla.booking.application.service;

import com.bookzilla.booking.application.port.out.ProviderRepository;
import com.bookzilla.booking.domain.Provider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProviderService {
    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public Provider createProvider(Long userId) {
        return providerRepository.save(new Provider(userId));
    }
}
