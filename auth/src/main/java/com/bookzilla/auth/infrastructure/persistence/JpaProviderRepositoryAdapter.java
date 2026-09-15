package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.application.port.out.ProviderRepository;
import com.bookzilla.auth.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaProviderRepositoryAdapter implements ProviderRepository {

    private final JpaProviderRepository jpaProviderRepository;

    @Autowired
    public JpaProviderRepositoryAdapter(JpaProviderRepository jpaProviderRepository) {
        this.jpaProviderRepository = jpaProviderRepository;
    }

    @Override
    public Provider save(Provider provider) {
        return jpaProviderRepository.save(provider);
    }

    @Override
    public Optional<Provider> findByUserId(Long userId) {
        return jpaProviderRepository.findByUserId(userId);
    }
}
