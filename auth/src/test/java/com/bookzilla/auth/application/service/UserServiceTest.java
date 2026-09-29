package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.Role;
import com.bookzilla.auth.domain.User;
import com.bookzilla.contracts.event.UserRegistered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully(){
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String password = "password123";
        String email = "john@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        // act
        userService.register(firstName, lastName, email);

        // assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getEmail()).isEqualTo(email);

        verify(applicationEventPublisher).publishEvent(any(UserRegistered.class));
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
                () -> userService.register(firstName, lastName, email)
        );
        verify(userRepository, never()).save(any(User.class));
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

}
