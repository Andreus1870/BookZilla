package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ClientService;
import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.DeleteUserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DeleteUserRepresentationEventListener {

    private final ClientService clientService;
    private final ProviderService providerService;

    public DeleteUserRepresentationEventListener(ClientService clientService, ProviderService providerService) {
        this.clientService = clientService;
        this.providerService = providerService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeleteUserRepresentation(DeleteUserRepresentation event){
        clientService.deleteClient(event.userId());
        providerService.deleteProvider(event.userId());
    }
}
