package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.bookzilla.auth.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaUserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    public JpaUserRepositoryAdapter(JpaUserRepository jpaUserRepository,
                                    UserMapper userMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userMapper = userMapper;
    }


    @Override
    public User save(User user) {
        return userMapper.toDomain(
                jpaUserRepository.save(
                    userMapper.toEntity(user)
                )
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return jpaUserRepository.findByEmailIgnoreCase(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> getUserByKeycloakId(UUID keycloakId){
        return jpaUserRepository.getUserByKeycloakId(keycloakId)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> getAllUsers() {
        return jpaUserRepository.findAll()
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteUserByEmail(String email) {

        jpaUserRepository.softDeleteByEmail(email);
    }
}
