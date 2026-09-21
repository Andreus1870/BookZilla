package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void shouldSave(){

        // arrange
        User user = new User(
                "John",
                "Doe",
                "hashedPassword",
                "john@example.com");

        when(jpaUserRepository.save(user)).thenReturn(user);

        // act
        User result = jpaUserRepositoryAdapter.save(user);

        // assert
        verify(jpaUserRepository).save(user);
        assertThat(result).isSameAs(user);
    }
}
