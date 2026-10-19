package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.dto.UserInfo;
import com.bookzilla.auth.application.dto.UserShortInfo;
import com.bookzilla.auth.application.service.UserAdministrationService;
import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.domain.BusinessRole;
import com.bookzilla.auth.infrastructure.web.dto.DeleteUserRequest;
import com.bookzilla.auth.infrastructure.web.dto.GetUserInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldReturnAllUsers() {

        // arrange

        String email = "user@example.com";
        String firstName = "John";
        String lastName = "Doe";
        Instant registrationDate = Instant.now();

        String moderatorEmail = "roosevelt@example.com";
        String moderatorFirstName = "Theodor";
        String moderatorLastName = "Roosevelt";
        Instant moderatorRegistrationDate = Instant.now();


        UserShortInfo userOne = new UserShortInfo(
                email,
                firstName,
                lastName,
                registrationDate,
                BusinessRole.USER
        );

        UserShortInfo userTwo = new UserShortInfo(
                moderatorEmail,
                moderatorFirstName,
                moderatorLastName,
                moderatorRegistrationDate,
                BusinessRole.MODERATOR
        );

        List<UserShortInfo> usersList = new ArrayList<>();

        usersList.add(userOne);
        usersList.add(userTwo);

        when(userAdministrationService.getAllUsers()).thenReturn(usersList);


        // act
        List<UserShortInfo> result = moderatorController.getAllUsers();

        // assert
        verify(userAdministrationService).getAllUsers();
        assertThat(result).isSameAs(usersList);


    }

    @Test
    void shouldDelegateUserInfoRetrievalToUserService(){

        // arrange

        String email = "roosevelt@example.com";
        String firstName = "Theodor";
        String lastName = "Roosevelt";
        Instant registrationDate = Instant.now();

        GetUserInfoRequest getUserInfoRequest = new GetUserInfoRequest(email);

        UserInfo userInfo = new UserInfo(
                firstName,
                lastName,
                email,
                null,
                null,
                null,
                registrationDate.toString(),
                BusinessRole.USER.name()
        );

        when(userService.getUserInfoByEmail(email)).thenReturn(userInfo);

        // act
        UserInfo result = moderatorController.getUserInfo(getUserInfoRequest);

        // assert
        verify(userService).getUserInfoByEmail(email);
        assertThat(result).isSameAs(userInfo);
    }
}
