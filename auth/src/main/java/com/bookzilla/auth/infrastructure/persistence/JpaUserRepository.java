package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    Optional<UserJpaEntity> getUserByKeycloakId(UUID keycloakId);

    void deleteUserByEmail(String email);

    List<UserJpaEntity> findAll();
}
