package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Provider;

public interface ProviderRepository {
    Provider save(Provider provider);

}