package com.bookzilla.booking.application.port.out;

import com.bookzilla.booking.domain.Client;


public interface ClientRepository {
    Client save(Client client);
}
