package com.bookzilla.booking.infrastructure.persistence.provider;

import com.bookzilla.booking.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProviderRepository extends JpaRepository<Provider, UUID> {
}
