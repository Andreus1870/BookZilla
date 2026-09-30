package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.web.dto.UserInfo;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(transactionManager = "authTransactionManager")
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdentityProvider identityProvider;

    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher,
                       IdentityProvider identityProvider) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.identityProvider = identityProvider;
    }

    public void register(String firstName,
                         String lastName,
                         String email,
                         String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        UUID keycloakUserId =
                identityProvider.createUser(firstName, lastName, email, password);

        User user = new User(firstName, lastName, email, keycloakUserId);

        userRepository.save(user);

        UserRegistered userRegisteredEvent =
                new UserRegistered(user.getId());

        applicationEventPublisher.publishEvent(userRegisteredEvent);
    }

    public UserInfo getUserInfoByKeycloakId(UUID uuid) {
        User user = userRepository.getUserByKeycloakId(uuid);

        return new UserInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                user.getRegistrationDate().toString()
        );
    }
}
