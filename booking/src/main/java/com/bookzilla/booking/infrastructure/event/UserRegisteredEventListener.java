package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {

    private final ProviderService providerService;

    public UserRegisteredEventListener(ProviderService providerService) {
        this.providerService = providerService;
    }

    @EventListener
    public void handleUserRegistered(UserRegistered event) {
        providerService.createProvider(event.userId());
    }
}