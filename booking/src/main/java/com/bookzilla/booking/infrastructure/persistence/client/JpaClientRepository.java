package com.bookzilla.booking.infrastructure.persistence.client;

import com.bookzilla.booking.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaClientRepository extends JpaRepository<Client, UUID> {
}
