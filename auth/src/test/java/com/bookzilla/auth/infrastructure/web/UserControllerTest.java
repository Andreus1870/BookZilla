package com.bookzilla.auth.infrastructure.web;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    void shouldRegister() {
        //arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "user",
                "green",
                "123",
                "email@example.com"
        );

        //act
        userController.register(registerRequest);

        //assert
        verify(userService).register(
                "user",
                "green",
                "123",
                "email@example.com"
        );
    }
}
