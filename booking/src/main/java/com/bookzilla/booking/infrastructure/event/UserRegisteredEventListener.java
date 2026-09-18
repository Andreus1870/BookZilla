package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventListener {

    private final ProviderService providerService;

    public UserRegisteredEventListener(ProviderService providerService) {
        this.providerService = providerService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegistered event) {
        providerService.createProvider(event.userId());
    }
}