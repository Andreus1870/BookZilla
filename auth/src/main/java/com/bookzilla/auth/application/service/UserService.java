package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.dto.UserInfo;
import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdentityProvider identityProvider;
    private final TransactionTemplate transactionTemplate;


    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher,
                       IdentityProvider identityProvider,
                       @Qualifier("userTransactionManager") PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.identityProvider = identityProvider;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void register(String firstName,
                         String lastName,
                         String email,
                         String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        UUID keycloakUserId = identityProvider.createIdentity(
                firstName, lastName, email, password
        );


        transactionTemplate.executeWithoutResult(status -> {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                identityProvider.deleteKeycloakUserRepresentation(
                                        keycloakUserId
                                );
                            }
                        }
                    }
            );

            User user = new User(
                    firstName, lastName, email, keycloakUserId
            );

            userRepository.save(user);

            applicationEventPublisher.publishEvent(
                    new UserRegistered(user.getId())
            );

        });

    }


    @Transactional(transactionManager = "userTransactionManager")
    public UserInfo getUserInfoByKeycloakId(UUID keycloakId) {

        User user = userRepository.getUserByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by Keycloak ID: " + keycloakId
                        )
                );

        return toUserInfo(user);
    }

    @Transactional(transactionManager = "userTransactionManager")
    public UserInfo getUserInfoByEmail(String email){

        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by email: " + email
                        )
                );

        return toUserInfo(user);
    }


    @Transactional(transactionManager = "userTransactionManager")
    public void setAdditionalUserInfo(UUID keycloakId,
                                      String country,
                                      String city,
                                      String phone) {

        User user = userRepository.getUserByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by Keycloak ID: " + keycloakId
                        )
                );

        user.updateAdditionalInfo(country, city, phone);

        userRepository.save(user);
    }


    private UserInfo toUserInfo(User user) {

        return new UserInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCountry(),
                user.getCity(),
                user.getRegistrationDate().toString(),
                identityProvider.getUserRoleByKeycloakId(user.getKeycloakId()).name()
        );

    }
}