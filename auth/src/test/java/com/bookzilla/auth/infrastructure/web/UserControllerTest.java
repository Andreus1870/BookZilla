package com.bookzilla.auth.infrastructure.web;

import com.bookzilla.auth.application.service.UserService;
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
                "123",
                "email@example.com"
        );

        //act
        authController.register(registerRequest);

        //assert
        verify(userService).register(
                "user",
                "green",
                "123",
                "email@example.com"
        );
    }
}
