package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserAdministrationService;
import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.DeleteUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ModeratorControllerTest {

    @Mock
    private UserAdministrationService userAdministrationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ModeratorController moderatorController;

    @Test
    void shouldDelegateUserDeletionToUserAdministrationService() {

        // arrange
        String email = "user@example.com";
        DeleteUserRequest deleteUserRequest =
                new DeleteUserRequest(email);

        // act
        moderatorController.deleteUser(deleteUserRequest);

        // assert
        verify(userAdministrationService).deleteUser(email);
    }
}
