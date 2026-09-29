package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
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

    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void register(String firstName,
                         String lastName,
                         String email) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        User user = new User(firstName, lastName, email);

        userRepository.save(user);

        UserRegistered userRegisteredEvent =
                new UserRegistered(user.getId());

        applicationEventPublisher.publishEvent(userRegisteredEvent);
    }

    public UserInfo getUserInfoByUuid(UUID uuid) {
        User user = userRepository.getUserByUuid(uuid);

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
