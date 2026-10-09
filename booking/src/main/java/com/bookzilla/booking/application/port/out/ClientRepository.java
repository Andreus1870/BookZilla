package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Client;

import java.util.UUID;


public interface ClientRepository {
    Client save(Client client);
    void deleteClient(UUID clientId);
}
