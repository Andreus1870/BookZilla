package com.bookzilla.auth.application.service;

import com.bookzilla.contracts.event.UserRegistered;
import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "authTransactionManager")
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void register(String firstName,
                         String lastName,
                         String password,
                         String email) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        User user = new User(firstName, lastName, encoder.encode(password), email);

        userRepository.save(user);

        UserRegistered userRegisteredEvent = new UserRegistered(user.getId());

        applicationEventPublisher.publishEvent(userRegisteredEvent);
    }
}
