package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.User;
import jakarta.validation.constraints.Email;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User getUserByKeycloakId(UUID keycloakId);
}
