package com.bookzilla.auth.application.port.out;

import com.bookzilla.auth.domain.User;
import jakarta.validation.constraints.Email;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
