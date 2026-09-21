package com.bookzilla.booking.infrastructure.event;

import com.bookzilla.booking.application.service.ProviderService;
import com.bookzilla.contracts.event.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegisteredEventListenerTest {

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private UserRegisteredEventListener userRegisteredEventListener;

    @Test
    void shouldHandleUserRegistered() {
        //arrange
        Long userId = 12L;
        UserRegistered userRegistered = new UserRegistered(userId);

        //act
        userRegisteredEventListener.handleUserRegistered(userRegistered);

        //assert
        verify(providerService).createProvider(userId);

    }
}
