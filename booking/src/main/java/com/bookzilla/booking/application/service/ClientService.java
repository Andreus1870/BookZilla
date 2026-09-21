package com.bookzilla.booking.application.service;

import com.bookzilla.booking.application.port.out.ClientRepository;
import com.bookzilla.booking.domain.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Long userId) {
        return clientRepository.save(new Client(userId));
    }
}
