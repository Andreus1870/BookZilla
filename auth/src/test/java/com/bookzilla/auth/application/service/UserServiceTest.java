package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.web.dto.UserInfo;
import com.bookzilla.contracts.event.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
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
        ArgumentCaptor<UserRegistered> eventCaptor = ArgumentCaptor.forClass(UserRegistered.class);

        verify(identityProvider).createIdentity(
                firstName, lastName, email, password
        );

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getKeycloakId()).isEqualTo(keycloakId);


        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        UserRegistered sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.userId()).isEqualTo(savedUser.getId());

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


    @Test
    void shouldGetUserInfoByKeycloakId() {

        // arrange
        UUID keycloakId = UUID.randomUUID();

        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                keycloakId
        );

        UserInfo userInfo = new UserInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,
                null,
                null,
                user.getRegistrationDate().toString()
        );

        when(userRepository.getUserByKeycloakId(keycloakId)).thenReturn(Optional.of(user));


        // act
        UserInfo result = userService.getUserInfoByKeycloakId(keycloakId);

        // assert
        verify(userRepository).getUserByKeycloakId(keycloakId);
        assertThat(result).isEqualTo(userInfo);
    }


    @Test
    void shouldThrowExceptionForNotFoundUser() {

        // arrange
        UUID keycloakId = UUID.randomUUID();

        Optional<User> optionalUser = Optional.empty();

        when(userRepository.getUserByKeycloakId(keycloakId)).thenReturn(optionalUser);

        // act and assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserInfoByKeycloakId(keycloakId)
        );

        assertThat(exception.getMessage())
                .isEqualTo("User not found by Keycloak ID: " + keycloakId);
    }

}
