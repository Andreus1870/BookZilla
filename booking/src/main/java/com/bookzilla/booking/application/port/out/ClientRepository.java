package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Client;

import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findByUserId(Long userId);
}
