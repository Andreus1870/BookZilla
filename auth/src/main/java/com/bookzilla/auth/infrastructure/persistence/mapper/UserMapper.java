package com.bookzilla.auth.infrastructure.persistence.mapper;

import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                user.getRegistrationDate(),
                user.isDeleted()
        );
    }

    public User toDomain(UserJpaEntity userJpaEntity) {
        return new User(
                userJpaEntity.getId(),
                userJpaEntity.getKeycloakId(),
                userJpaEntity.getFirstName(),
                userJpaEntity.getLastName(),
                userJpaEntity.getEmail(),
                userJpaEntity.getPhone(),
                userJpaEntity.getCountry(),
                userJpaEntity.getCity(),
                userJpaEntity.getRegistrationDate(),
                userJpaEntity.isDeleted()
        );
    }

}
