package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.CannotDowngradeAdminException;
import com.bookzilla.auth.application.exception.CannotDowngradeUserException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.BusinessRole;
import com.bookzilla.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    IdentityProvider identityProvider;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserRoleService userRoleService;

    @Test
    void shouldSuccessfullyDowngradeModerator() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        Optional<User> optionalUser = Optional.of(
                new User(
                    firstName,
                    lastName,
                    email,
                    keycloakId
                )
        );

        when(userRepository.getUserByEmail(email)).thenReturn(optionalUser);

        when(identityProvider.getUserRoleByKeycloakId(keycloakId)).thenReturn(BusinessRole.MODERATOR);

        // act and assert
        userRoleService.downgradeModerator(email);

        verify(identityProvider).downgradeModerator(keycloakId);

    }

    @Test
    void shouldThrowExceptionWhenTryToDowngradeUser() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        Optional<User> optionalUser = Optional.of(
                new User(
                        firstName,
                        lastName,
                        email,
                        keycloakId
                )
        );

        when(userRepository.getUserByEmail(email)).thenReturn(optionalUser);

        when(identityProvider.getUserRoleByKeycloakId(keycloakId)).thenReturn(BusinessRole.USER);

        // act and assert
        assertThrows(
                CannotDowngradeUserException.class,
                () -> userRoleService.downgradeModerator(email)
        );

        verify(identityProvider, never()).downgradeModerator(keycloakId);
    }

    @Test
    void shouldThrowExceptionWhenTryToDowngradeAdmin() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        Optional<User> optionalUser = Optional.of(
                new User(
                        firstName,
                        lastName,
                        email,
                        keycloakId
                )
        );

        when(userRepository.getUserByEmail(email)).thenReturn(optionalUser);

        when(identityProvider.getUserRoleByKeycloakId(keycloakId)).thenReturn(BusinessRole.ADMIN);

        // act and assert
        assertThrows(
                CannotDowngradeAdminException.class,
                () -> userRoleService.downgradeModerator(email)
        );

        verify(identityProvider, never()).downgradeModerator(keycloakId);
    }

    @Test
    void shouldSuccessfullyPromoteUserToModerator() {

        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        UUID keycloakId = UUID.randomUUID();

        Optional<User> optionalUser = Optional.of(
                new User(
                        firstName,
                        lastName,
                        email,
                        keycloakId
                )
        );

        when(userRepository.getUserByEmail(email)).thenReturn(optionalUser);

        when(identityProvider.getUserRoleByKeycloakId(keycloakId)).thenReturn(BusinessRole.USER);

        // act and assert
        userRoleService.promoteToModerator(email);

        verify(identityProvider).promoteToModerator(keycloakId);

    }
}
