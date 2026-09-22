package com.bookzilla.booking.infrastructure.persistence.provider;

import com.bookzilla.booking.application.port.out.ProviderRepository;
import com.bookzilla.booking.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaProviderRepositoryAdapter implements ProviderRepository {

    private final JpaProviderRepository providerRepository;

    @Autowired
    public JpaProviderRepositoryAdapter(JpaProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }
}
