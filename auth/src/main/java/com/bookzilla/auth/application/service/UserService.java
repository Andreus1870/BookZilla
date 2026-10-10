package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.CannotDeleteModeratorException;
import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.application.dto.UserInfo;
import com.bookzilla.auth.application.dto.UserShortInfo;
import com.bookzilla.contracts.event.DeleteUserRepresentation;
import com.bookzilla.contracts.event.UserRegistered;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(transactionManager = "userTransactionManager")
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdentityProvider identityProvider;


    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher,
                       IdentityProvider keycloakIdentityProvider) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.identityProvider = keycloakIdentityProvider;
    }


    public void register(String firstName,
                         String lastName,
                         String email,
                         String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        UUID keycloakUserId =
                identityProvider.createIdentity(firstName, lastName, email, password);

        User user = new User(firstName, lastName, email, keycloakUserId);

        userRepository.save(user);

        UserRegistered userRegisteredEvent =
                new UserRegistered(user.getId());

        applicationEventPublisher.publishEvent(userRegisteredEvent);
    }


    public UserInfo getUserInfoByKeycloakId(UUID keycloakId) {

        User user = userRepository.getUserByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by Keycloak ID: " + keycloakId
                        )
                );

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
    }


    public void deleteUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by email: " + email
                        )
                );

        if (!identityProvider.hasUserRole(email)) {
            throw new CannotDeleteModeratorException("Moderators cannot delete other moderators");
        }

        identityProvider.deleteKeycloakUserRepresentation(email);

        userRepository.deleteUserByEmail(email);

        UUID userToDeleteUuid = user.getId();

        DeleteUserRepresentation deleteUserRepresentationEvent =
                new DeleteUserRepresentation(userToDeleteUuid);

        applicationEventPublisher.publishEvent(deleteUserRepresentationEvent);

    }

    public List<UserShortInfo> getAllUsers() {

        List<User> users = userRepository.getAllUsers();

        return users.stream()
                .map(user -> new UserShortInfo(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRegistrationDate()
                ))
                .toList();
    }

    public UUID getUserKeycloakIdByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found by email: " + email)
                );

        return user.getKeycloakId();
    }


}