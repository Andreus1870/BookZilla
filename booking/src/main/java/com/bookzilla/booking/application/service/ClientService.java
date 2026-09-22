package com.bookzilla.booking.application.service;

import com.bookzilla.booking.application.port.out.ClientRepository;
import com.bookzilla.booking.domain.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(transactionManager = "bookingTransactionManager")
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(UUID userId) {
        return clientRepository.save(new Client(userId));
    }
}
