package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.dto.UserShortInfo;
import com.bookzilla.auth.application.exception.CannotDeleteModeratorException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.application.service.user.UserAdministrationService;
import com.bookzilla.auth.domain.user.BusinessRole;
import com.bookzilla.auth.domain.user.User;
import com.bookzilla.contracts.event.DeleteUserRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void shouldGetAllUsersSuccessfully() {
        // arrange
        UUID firstKeycloakId = UUID.randomUUID();
        UUID secondKeycloakId = UUID.randomUUID();

        User firstUser = new User(
                "John",
                "Doe",
                "john@example.com",
                firstKeycloakId
        );
        User secondUser = new User(
                "Jane",
                "Smith",
                "jane@example.com",
                secondKeycloakId
        );

        List<User> users = List.of(firstUser, secondUser);

        Map<UUID, BusinessRole> rolesByKeycloakIds = Map.of(
                firstKeycloakId, BusinessRole.USER,
                secondKeycloakId, BusinessRole.MODERATOR
        );

        when(userRepository.getAllUsers()).thenReturn(users);
        when(identityProvider.getUsersRolesByKeycloakIds(
                List.of(firstKeycloakId, secondKeycloakId)
        )).thenReturn(rolesByKeycloakIds);

        // act
        List<UserShortInfo> result = userAdministrationService.getAllUsers();

        // assert
        assertEquals(2, result.size());

        assertEquals("john@example.com", result.get(0).email());
        assertEquals("John", result.get(0).firstName());
        assertEquals("Doe", result.get(0).lastName());
        assertEquals(BusinessRole.USER, result.get(0).role());

        assertEquals("jane@example.com", result.get(1).email());
        assertEquals("Jane", result.get(1).firstName());
        assertEquals("Smith", result.get(1).lastName());
        assertEquals(BusinessRole.MODERATOR, result.get(1).role());

        verify(userRepository).getAllUsers();
        verify(identityProvider).getUsersRolesByKeycloakIds(
                List.of(firstKeycloakId, secondKeycloakId)
        );

        assertEquals(firstUser.getRegistrationDate(), result.get(0).registrationDate());
        assertEquals(secondUser.getRegistrationDate(), result.get(1).registrationDate());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        // arrange
        when(userRepository.getAllUsers()).thenReturn(List.of());
        when(identityProvider.getUsersRolesByKeycloakIds(List.of()))
                .thenReturn(Map.of());

        // act
        List<UserShortInfo> result = userAdministrationService.getAllUsers();

        // assert
        assertTrue(result.isEmpty());

        verify(userRepository).getAllUsers();
        verify(identityProvider).getUsersRolesByKeycloakIds(List.of());
    }
}
