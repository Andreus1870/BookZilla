package com.bookzilla.auth.infrastructure.persistence;

import com.bookzilla.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
