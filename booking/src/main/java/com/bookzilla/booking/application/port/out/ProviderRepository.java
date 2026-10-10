package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Provider;

import java.util.UUID;

public interface ProviderRepository {
    Provider save(Provider provider);
    void deleteProvider(UUID providerId);
}