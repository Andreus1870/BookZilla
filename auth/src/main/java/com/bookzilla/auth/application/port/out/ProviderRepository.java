package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.Provider;

import java.util.Optional;

public interface ProviderRepository {
    Provider save(Provider provider);
    Optional<Provider> findByUserId(Long userId);
}
