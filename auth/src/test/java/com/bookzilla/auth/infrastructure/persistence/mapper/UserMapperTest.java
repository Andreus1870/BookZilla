package com.bookzilla.auth.infrastructure.persistence.mapper;

import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapDomainToEntity() {
        // arrange
        UUID id = UUID.randomUUID();
        UUID keycloakId = UUID.randomUUID();
        Instant registrationDate = Instant.now();

        User user = new User(
                id,
                keycloakId,
                "John",
                "Smith",
                "john.smith@example.com",
                "+48123456789",
                "Poland",
                "Poznan",
                registrationDate
        );

        // act
        UserJpaEntity result = userMapper.toEntity(user);

        // assert
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(result.getPhone()).isEqualTo("+48123456789");
        assertThat(result.getCountry()).isEqualTo("Poland");
        assertThat(result.getCity()).isEqualTo("Poznan");
        assertThat(result.getRegistrationDate()).isEqualTo(registrationDate);
    }

    @Test
    void shouldMapEntityToDomain() {
        // arrange
        UUID id = UUID.randomUUID();
        UUID keycloakId = UUID.randomUUID();
        Instant registrationDate = Instant.now();

        UserJpaEntity userJpaEntity = new UserJpaEntity(
                id,
                keycloakId,
                "John",
                "Smith",
                "john.smith@example.com",
                "+48123456789",
                "Poland",
                "Poznan",
                registrationDate
        );

        // act
        User result = userMapper.toDomain(userJpaEntity);

        // assert
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(result.getPhone()).isEqualTo("+48123456789");
        assertThat(result.getCountry()).isEqualTo("Poland");
        assertThat(result.getCity()).isEqualTo("Poznan");
        assertThat(result.getRegistrationDate()).isEqualTo(registrationDate);
    }
}