package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.integration.UserService;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldDelegateRegistrationToUserService() {
        //arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "user",
                "green",
                "email@example.com",
                "123456789"
        );

        //act
        authController.register(registerRequest);

        //assert
        verify(userService).register(
                "user",
                "green",
                "email@example.com",
                "123456789"
        );
    }
}
