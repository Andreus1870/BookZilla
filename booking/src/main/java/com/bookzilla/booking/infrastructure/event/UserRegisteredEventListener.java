package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ClientService;
import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventListener {

    private final ProviderService providerService;
    private final ClientService clientService;

    public UserRegisteredEventListener(ProviderService providerService, ClientService clientService) {
        this.providerService = providerService;
        this.clientService = clientService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegistered event) {
        providerService.createProvider(event.userId());
        clientService.createClient(event.userId());
    }
}