package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Provider;

import java.util.Optional;

public interface ProviderRepository {

    Provider save(Provider provider);

    Optional<Provider> findByUserId(Long userId);
}