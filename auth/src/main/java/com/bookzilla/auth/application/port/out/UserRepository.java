package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByKeycloakId(UUID keycloakId);

    boolean deleteUserByEmail(String email);

    List<User> getAllUsers();
}
