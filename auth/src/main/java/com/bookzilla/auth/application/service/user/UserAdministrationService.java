package com.bookzilla.auth.application.service.user;

import com.bookzilla.auth.application.dto.UserShortInfo;
import com.bookzilla.auth.application.exception.CannotDeleteModeratorException;
import com.bookzilla.auth.application.exception.UserDeletionException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.user.BusinessRole;
import com.bookzilla.auth.domain.user.User;
import com.bookzilla.contracts.event.DeleteUserRepresentation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(transactionManager = "userTransactionManager")
public class UserAdministrationService {

    private final UserRepository userRepository;
    private final IdentityProvider identityProvider;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserAdministrationService(UserRepository userRepository,
                                     IdentityProvider identityProvider,
                                     ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.identityProvider = identityProvider;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void deleteUser(String email) {

        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found by email: " + email
                        )
                );

        UUID keycloakId = user.getKeycloakId();

        if (!identityProvider.hasUserRole(keycloakId)) {
            throw new CannotDeleteModeratorException("Moderators cannot delete other moderators");
        }

        if (!userRepository.deleteUserByEmail(email)) {
            throw new UserDeletionException(
                    "Failed to delete user: " + email
            );
        }

        identityProvider.deleteKeycloakUserRepresentation(keycloakId);

        UUID userToDeleteUuid = user.getId();

        DeleteUserRepresentation deleteUserRepresentationEvent =
                new DeleteUserRepresentation(userToDeleteUuid);

        applicationEventPublisher.publishEvent(deleteUserRepresentationEvent);

    }

    public List<UserShortInfo> getAllUsers() {

        List<User> users = userRepository.getAllUsers();

        Map<UUID, BusinessRole> rolesByKeycloakIds =
                identityProvider.getUsersRolesByKeycloakIds(users.stream()
                        .map(User::getKeycloakId)
                        .toList());

        return users.stream()
                .map(user -> new UserShortInfo(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRegistrationDate(),
                        rolesByKeycloakIds.get(user.getKeycloakId())
                ))
                .toList();
    }

}
