package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.contracts.event.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private IdentityProvider identityProvider;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully(){
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(identityProvider.createIdentity(firstName, lastName, email, password))
                .thenReturn(keycloakId);

        // act
        userService.register(firstName, lastName, email, password);

        // assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(identityProvider).createIdentity(
                firstName, lastName, email, password
        );

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getKeycloakId()).isEqualTo(keycloakId);

        verify(applicationEventPublisher).publishEvent(any(UserRegistered.class));
    }

    @Test
    void shouldNotSaveUserWhenIdentityCreationFails() {
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";
        String email = "john@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(identityProvider.createIdentity(firstName, lastName, email, password))
                .thenThrow(new RuntimeException("Identity creation failed"));


        // act and assert
        assertThrows(
                RuntimeException.class,
                () -> userService.register(firstName, lastName, email, password)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(applicationEventPublisher, never()).publishEvent(any());

    }


    @Test
    void shouldRejectRegistrationWhenEmailExists(){
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";
        String email = "john@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // act and assert
        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> userService.register(firstName, lastName, email, password)
        );
        verify(userRepository, never()).save(any(User.class));
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

}
