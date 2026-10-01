package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JpaUserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private JpaUserRepositoryAdapter jpaUserRepositoryAdapter;

    @Test
    void shouldDelegateSaveToJpaRepository(){

        // arrange
        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                UUID.randomUUID());

        when(jpaUserRepository.save(user)).thenReturn(user);

        // act
        User result = jpaUserRepositoryAdapter.save(user);

        // assert
        verify(jpaUserRepository).save(user);
        assertThat(result).isSameAs(user);
    }

    @Test
    void shouldFindUserByKeycloakId() {
        // arrange
        UUID keycloakId = UUID.randomUUID();
        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                keycloakId);

        when(jpaUserRepository.getUserByKeycloakId(keycloakId)).thenReturn(Optional.of(user));

        // act
        Optional<User> result = jpaUserRepositoryAdapter.getUserByKeycloakId(keycloakId);

        // assert
        verify(jpaUserRepository).getUserByKeycloakId(keycloakId);
        assertThat(result).containsSame(user);
    }

    @Test
    void shouldCheckIfEmailExists() {
        // arrange
        String email = "user@example.com";
        boolean doesExist = true;

        when(jpaUserRepository.existsByEmail(email)).thenReturn(doesExist);

        // act
        boolean result = jpaUserRepositoryAdapter.existsByEmail(email);

        // assert
        verify(jpaUserRepository).existsByEmail(email);
        assertThat(result).isSameAs(doesExist);
    }

    @Test
    void shouldFindUserByEmail() {

        // arrange
        String email = "user@example.com";
        User user = new User(
                "John",
                "Doe",
                "user@example.com",
                UUID.randomUUID());

        when(jpaUserRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));

        // act
        Optional<User> result = jpaUserRepositoryAdapter.findByEmail(email);

        // assert
        verify(jpaUserRepository).findByEmailIgnoreCase(email);
        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(user);
    }
}
