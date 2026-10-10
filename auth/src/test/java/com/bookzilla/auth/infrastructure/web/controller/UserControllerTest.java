package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.SetAdditionalInfoRequest;
import com.bookzilla.auth.application.dto.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldGetUserInfo() {
        // arrange
        UUID keycloakId = UUID.randomUUID();

        UserInfo userInfo = new UserInfo(
                "John",
                "Doe",
                "john@example.com",
                null,
                null,
                null,
                "2026-09-30T10:00:00Z"
        );

        when(authentication.getName()).thenReturn(String.valueOf(keycloakId));
        when(userService.getUserInfoByKeycloakId(keycloakId)).thenReturn(userInfo);


        //act
        UserInfo result = userController.getUserInfo(authentication);

        //assert
        verify(authentication).getName();
        verify(userService).getUserInfoByKeycloakId(keycloakId);
        assertThat(result).isEqualTo(userInfo);


    }


    @Test
    void shouldSetAdditionalUserInfo() {
        // arrange
        UUID keycloakId = UUID.randomUUID();

        String country = "Ukraine";
        String city = "Lviv";
        String phone = "+380123456789";

        SetAdditionalInfoRequest request =
                new SetAdditionalInfoRequest(phone, country, city);

        when(authentication.getName()).thenReturn(String.valueOf(keycloakId));

        // act
        userController.setAdditionalUserInfo(authentication, request);

        // assert
        verify(authentication).getName();
        verify(userService).setAdditionalUserInfo(
                keycloakId,
                country,
                city,
                phone
        );
    }
}
