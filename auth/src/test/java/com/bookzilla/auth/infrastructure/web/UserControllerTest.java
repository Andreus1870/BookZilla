package com.bookzilla.auth.infrastructure.web;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.controller.AuthController;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    void shouldRegister() {
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
