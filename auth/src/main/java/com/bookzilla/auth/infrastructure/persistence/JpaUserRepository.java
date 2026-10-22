package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    Optional<UserJpaEntity> getUserByKeycloakId(UUID keycloakId);

    @Modifying
    @Query("""
        UPDATE UserJpaEntity u
        SET u.isDeleted = true
        WHERE LOWER(u.email) = LOWER(:email)
            AND u.isDeleted = false
    """)
    int softDeleteByEmail(@Param("email") String email);
}
