package com.bookzilla.auth.application.service.user;

import com.bookzilla.auth.application.dto.UserInfo;
import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.exception.UserNotFoundException;
import com.bookzilla.auth.application.port.out.identity.IdentityProvider;
import com.bookzilla.auth.application.port.out.retryable.RetryableOperationRepository;
import com.bookzilla.auth.application.port.out.user.UserRepository;
import com.bookzilla.auth.domain.operation.OperationStatus;
import com.bookzilla.auth.domain.operation.OperationType;
import com.bookzilla.auth.domain.operation.RetryableOperation;
import com.bookzilla.auth.domain.operation.payload.DeleteKeycloakUserPayload;
import com.bookzilla.auth.domain.user.User;
import com.bookzilla.auth.infrastructure.keycloak.exception.KeycloakUserDeletionException;
import com.bookzilla.contracts.event.UserRegistered;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IdentityProvider identityProvider;
    private final RetryableOperationRepository retryableOperationRepository;
    private final TransactionTemplate transactionTemplate;

    private final ObjectMapper objectMapper;

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);


    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher applicationEventPublisher,
                       IdentityProvider identityProvider,
                       RetryableOperationRepository retryableOperationRepository,
                       @Qualifier("userTransactionManager") PlatformTransactionManager transactionManager,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.identityProvider = identityProvider;
        this.retryableOperationRepository = retryableOperationRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.objectMapper = objectMapper;
    }

    public void register(String firstName,
                         String lastName,
                         String email,
                         String password) {

        if (userRepository.existsByEmail(email)) {

            log.warn("User registration rejected: email is already registered");

            throw new EmailAlreadyRegisteredException(
                    "Email is already registered!"
            );
        }

        UUID keycloakUserId = identityProvider.createIdentity(
                firstName, lastName, email, password
        );

        log.info("identity created: keycloakUserId={}", keycloakUserId);


        transactionTemplate.executeWithoutResult(status -> {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {

                            if (status == STATUS_ROLLED_BACK) {

                                log.warn(
                                        "User database transaction rolled back." +
                                                " Starting Keycloak identity cleanup: keycloakUserId={}",
                                        keycloakUserId);

                                try {

                                    identityProvider.deleteKeycloakUserRepresentation(
                                            keycloakUserId
                                    );

                                    log.info("Compensation was executed successfully. " +
                                                    "Keycloak identity was deleted: keycloakUserId={}",
                                            keycloakUserId);

                                } catch (KeycloakUserDeletionException exception) {

                                    log.error("An error occurred while trying to delete Keycloak identity: keycloakUserId={}",
                                            keycloakUserId,
                                            exception);


                                    try {

                                        String payload;

                                        payload = objectMapper.writeValueAsString(
                                                new DeleteKeycloakUserPayload(keycloakUserId)
                                        );


                                        Instant now = Instant.now();

                                        RetryableOperation retryableOperation = new RetryableOperation(
                                                UUID.randomUUID(),
                                                OperationType.DELETE_KEYCLOAK_USER,
                                                payload,
                                                OperationStatus.PENDING,
                                                0,
                                                now,
                                                exception.getMessage() != null
                                                        ? exception.getMessage()
                                                        : exception.getClass().getSimpleName(),
                                                now,
                                                now
                                        );

                                        retryableOperationRepository.save(retryableOperation);

                                        log.info(
                                                "Retryable operation saved: operationId={}, operationType={}, keycloakUserId={}",
                                                retryableOperation.getId(),
                                                retryableOperation.getOperationType(),
                                                keycloakUserId
                                        );

                                    }   catch (Exception retrySaveException){

                                        log.error(
                                                "Failed to save retryable operation: keycloakUserId={}",
                                                keycloakUserId,
                                                retrySaveException
                                        );
                                    }



                                }
                            } else if (status == STATUS_COMMITTED) {

                                log.info("User registration transaction committed: keycloakUserId={}", keycloakUserId);
                            }
                        }
                    }
            );

            User user = new User(
                    firstName, lastName, email, keycloakUserId
            );

            userRepository.save(user);

            log.info("User saved to database: userId={}", user.getId());

            applicationEventPublisher.publishEvent(
                    new UserRegistered(user.getId())
            );

            log.info("UserRegistered event published: userId={}", user.getId());

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

        log.info("Additional user information updated: keycloakUserId={}", keycloakId);
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