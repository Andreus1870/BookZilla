package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.domain.user.User;
import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.bookzilla.auth.infrastructure.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserRepositoryAdapterTest {


    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JpaUserRepositoryAdapter jpaUserRepositoryAdapter;


    @Test
    void shouldMapSaveAndReturnDomainUser(){

        // arrange
        UUID keycloakId = UUID.randomUUID();

        User user = new User(
                "John",
                "Doe",
                "jdoe@example.com",
                keycloakId
        );

        UUID userId = user.getId();

        UserJpaEntity jpaUser = new UserJpaEntity(
                userId,
                keycloakId,
                "John",
                "Doe",
                "jdoe@example.com",
                null,
                null,
                null,
                user.getRegistrationDate()
        );

        when(userMapper.toEntity(user)).thenReturn(jpaUser);
        when(jpaUserRepository.save(jpaUser)).thenReturn(jpaUser);
        when(userMapper.toDomain(jpaUser)).thenReturn(user);


        // act
        User result = jpaUserRepositoryAdapter.save(user);

        // assert
        verify(jpaUserRepository).save(jpaUser);
        verify(userMapper).toEntity(user);
        verify(userMapper).toDomain(jpaUser);
        assertThat(result).isSameAs(user);
    }


    @Test
    void shouldFindUserByKeycloakId() {

        // arrange
        UUID keycloakId = UUID.randomUUID();

        User user = new User(
                "John",
                "Doe",
                "jdoe@example.com",
                keycloakId
        );

        UUID userId = user.getId();

        UserJpaEntity jpaUser = new UserJpaEntity(
                userId,
                keycloakId,
                "John",
                "Doe",
                "jdoe@example.com",
                null,
                null,
                null,
                user.getRegistrationDate()
        );

        Optional<UserJpaEntity> optionalUserJpa = Optional.of(jpaUser);

        when(jpaUserRepository.getUserByKeycloakId(keycloakId)).thenReturn(optionalUserJpa);
        when(userMapper.toDomain(jpaUser)).thenReturn(user);


        // act
        Optional<User> result = jpaUserRepositoryAdapter.getUserByKeycloakId(keycloakId);

        // assert
        verify(jpaUserRepository).getUserByKeycloakId(keycloakId);
        verify(userMapper).toDomain(jpaUser);

        assertThat(result).containsSame(user);
    }


    @Test
    void shouldReturnEmptyWhenUserNotFoundByKeycloakId() {

        // arrange
        UUID keycloakId = UUID.randomUUID();

        when(jpaUserRepository.getUserByKeycloakId(keycloakId)).thenReturn(Optional.empty());

        // act
        Optional<User> result = jpaUserRepositoryAdapter.getUserByKeycloakId(keycloakId);

        // assert
        verify(jpaUserRepository).getUserByKeycloakId(keycloakId);
        verifyNoInteractions(userMapper);
        assertThat(result).isEmpty();

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
        assertThat(result).isEqualTo(doesExist);
    }


    @Test
    void shouldFindUserByEmail() {

        // arrange
        UUID keycloakId = UUID.randomUUID();
        String email = "jdoe@example.com";

        User user = new User(
                "John",
                "Doe",
                "jdoe@example.com",
                keycloakId
        );

        UUID userId = user.getId();

        UserJpaEntity jpaUser = new UserJpaEntity(
                userId,
                keycloakId,
                "John",
                "Doe",
                "jdoe@example.com",
                null,
                null,
                null,
                user.getRegistrationDate()
        );

        Optional<UserJpaEntity> optionalUserJpa = Optional.of(jpaUser);

        when(jpaUserRepository.findByEmailIgnoreCase(email)).thenReturn(optionalUserJpa);
        when(userMapper.toDomain(jpaUser)).thenReturn(user);


        // act
        Optional<User> result = jpaUserRepositoryAdapter.getUserByEmail(email);

        // assert
        verify(jpaUserRepository).findByEmailIgnoreCase(email);
        verify(userMapper).toDomain(jpaUser);

        assertThat(result).containsSame(user);
    }


    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {

        // arrange
        String email = "john@example.com";

        when(jpaUserRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // act
        Optional<User> result = jpaUserRepositoryAdapter.getUserByEmail(email);

        // assert
        verify(jpaUserRepository).findByEmailIgnoreCase(email);
        verifyNoInteractions(userMapper);
        assertThat(result).isEmpty();

    }

    @Test
    void shouldDeleteUserByEmail() {
        // arrange
        String email = "john@example.com";

        // act
        jpaUserRepositoryAdapter.deleteUserByEmail(email);

        // assert
        verify(jpaUserRepository).deleteUserByEmail(email);

    }


    @Test
    void shouldReturnAllUsers() {
        // arrange

        //user1
        UUID keycloakIdOne = UUID.randomUUID();

        User userOne = new User(
                "John",
                "Doe",
                "jdoe@example.com",
                keycloakIdOne
        );

        UUID userOneId = userOne.getId();

        UserJpaEntity jpaUserOne = new UserJpaEntity(
                userOneId,
                keycloakIdOne,
                "John",
                "Doe",
                "jdoe@example.com",
                null,
                null,
                null,
                userOne.getRegistrationDate()
        );


        //user2
        UUID keycloakIdTwo = UUID.randomUUID();

        User userTwo = new User(
                "John",
                "Doe",
                "jdoe2@example.com",
                keycloakIdTwo
        );

        UUID userTwoId = userTwo.getId();

        UserJpaEntity jpaUserTwo = new UserJpaEntity(
                userTwoId,
                keycloakIdTwo,
                "John",
                "Doe",
                "jdoe2@example.com",
                null,
                null,
                null,
                userTwo.getRegistrationDate()
        );

        List<UserJpaEntity> userJpaList = List.of(jpaUserOne, jpaUserTwo);

        when(jpaUserRepository.findAll()).thenReturn(userJpaList);
        when(userMapper.toDomain(jpaUserOne)).thenReturn(userOne);
        when(userMapper.toDomain(jpaUserTwo)).thenReturn(userTwo);


        //act
        List<User> result = jpaUserRepositoryAdapter.getAllUsers();


        //assert
        verify(jpaUserRepository).findAll();
        verify(userMapper).toDomain(jpaUserOne);
        verify(userMapper).toDomain(jpaUserTwo);

        assertThat(result).containsExactly(userOne, userTwo);


    }


    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        // arrange

        List<UserJpaEntity> userJpaList = new ArrayList<>();

        when(jpaUserRepository.findAll()).thenReturn(userJpaList);


        //act
        List<User> result = jpaUserRepositoryAdapter.getAllUsers();


        //assert
        verify(jpaUserRepository).findAll();
        verifyNoInteractions(userMapper);

        assertThat(result).containsExactly();
    }
}
