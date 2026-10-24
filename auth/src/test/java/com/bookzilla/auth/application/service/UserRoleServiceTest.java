package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.AlreadyIsModeratorException;
import com.bookzilla.auth.application.exception.CannotDowngradeAdminException;
import com.bookzilla.auth.application.exception.CannotDowngradeUserException;
import com.bookzilla.auth.application.exception.CannotPromoteAdminException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.application.service.user.UserRoleService;
import com.bookzilla.auth.domain.user.BusinessRole;
import com.bookzilla.auth.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
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


    private final String email = "john@example.com";
    private final UUID keycloakId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        String firstName = "John";
        String lastName = "Doe";
        User user = new User(firstName, lastName, email, keycloakId);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));
    }

    @Test
    void shouldSuccessfullyDowngradeModerator() {

        // arrange
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.MODERATOR);

        // act and assert
        userRoleService.downgradeModerator(email);

        verify(identityProvider).downgradeModerator(keycloakId);

    }

    @Test
    void shouldThrowExceptionWhenTryToDowngradeUser() {

        // arrange
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.USER);

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
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.ADMIN);

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
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.USER);

        // act and assert
        userRoleService.promoteToModerator(email);

        verify(identityProvider).promoteToModerator(keycloakId);

    }

    @Test
    void shouldThrowExceptionWhenTryToPromoteModerator() {

        // arrange
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.MODERATOR);

        // act and assert
        assertThrows(
                AlreadyIsModeratorException.class,
                () -> userRoleService.promoteToModerator(email)
        );

        verify(identityProvider, never()).promoteToModerator(keycloakId);
    }

    @Test
    void shouldThrowExceptionWhenTryToPromoteAdmin() {

        // arrange
        when(identityProvider.getUserRoleByKeycloakId(keycloakId))
                .thenReturn(BusinessRole.ADMIN);

        // act and assert
        assertThrows(
                CannotPromoteAdminException.class,
                () -> userRoleService.promoteToModerator(email)
        );

        verify(identityProvider, never()).promoteToModerator(keycloakId);
    }
}
