package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.CannotDeleteModeratorException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.contracts.event.DeleteUserRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAdministrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserAdministrationService userAdministrationService;

    @Test
    void shouldDeleteUserSuccessfully() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        User user = new User(
                firstName,
                lastName,
                email,
                keycloakId
        );

        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(identityProvider.hasUserRole(keycloakId)).thenReturn(true);

        // act
        userAdministrationService.deleteUser(email);

        // assert
        verify(userRepository).getUserByEmail(email);
        verify(identityProvider).hasUserRole(keycloakId);
        verify(identityProvider).deleteKeycloakUserRepresentation(keycloakId);
        verify(userRepository).deleteUserByEmail(email);
        verify(applicationEventPublisher).publishEvent(
                new DeleteUserRepresentation(user.getId())
        );

    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {

        // arrange
        String email = "john@example.com";

        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(
                UserNotFoundException.class,
                () -> userAdministrationService.deleteUser(email)
        );

        verify(userRepository).getUserByEmail(email);
        verify(identityProvider, never()).deleteKeycloakUserRepresentation(any());
        verify(userRepository, never()).deleteUserByEmail(anyString());
        verifyNoInteractions(applicationEventPublisher);

    }

    @Test
    void shouldThrowExceptionWhenTryToDeleteModerator() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        User user = new User(
                firstName,
                lastName,
                email,
                keycloakId
        );

        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(identityProvider.hasUserRole(keycloakId)).thenReturn(false);

        // act and assert
        assertThrows(
                CannotDeleteModeratorException.class,
                () -> userAdministrationService.deleteUser(email)
        );

        verify(userRepository).getUserByEmail(email);
        verify(identityProvider).hasUserRole(keycloakId);
        verify(identityProvider, never()).deleteKeycloakUserRepresentation(any());
        verify(userRepository, never()).deleteUserByEmail(anyString());
        verifyNoInteractions(applicationEventPublisher);
    }

}
