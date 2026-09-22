package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ClientService;
import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegisteredEventListenerTest {

    @Mock
    private ProviderService providerService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private UserRegisteredEventListener userRegisteredEventListener;

    @Test
    void shouldHandleUserRegistered() {
        //arrange
        UUID id = UUID.randomUUID();
        UserRegistered userRegistered = new UserRegistered(id);

        //act
        userRegisteredEventListener.handleUserRegistered(userRegistered);

        //assert
        verify(providerService).createProvider(id);
        verify(clientService).createClient(id);

    }
}
