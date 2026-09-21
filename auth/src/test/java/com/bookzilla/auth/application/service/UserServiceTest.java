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
    private PasswordEncoder passwordEncoder;

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
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        // act
        userService.register(firstName, lastName, password, email);

        // assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);

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
                () -> userService.register(firstName, lastName, password, email)
        );
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

}
