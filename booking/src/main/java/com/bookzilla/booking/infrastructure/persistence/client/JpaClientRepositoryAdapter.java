package com.bookzilla.booking.infrastructure.persistence.client;

import com.bookzilla.booking.application.port.out.ClientRepository;
import com.bookzilla.booking.domain.Client;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaClientRepositoryAdapter implements ClientRepository {

    private final JpaClientRepository jpaClientRepository;

    public JpaClientRepositoryAdapter(JpaClientRepository jpaClientRepository) {
        this.jpaClientRepository = jpaClientRepository;
    }

    public Client save(Client client){
        return jpaClientRepository.save(client);
    }

    public Optional<Client> findByUserId(Long userId) {
        return jpaClientRepository.findByUserId(userId);
    }
}
