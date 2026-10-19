package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserRoleService;
import com.bookzilla.auth.infrastructure.web.dto.DowngradeModeratorRequest;
import com.bookzilla.auth.infrastructure.web.dto.PromoteToModeratorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    UserRoleService userRoleService;

    @InjectMocks
    AdminController adminController;

    @Test
    void shouldDelegateModeratorDowngradeToUserRoleService() {

        String email = "jdoe@example.com";

        DowngradeModeratorRequest downgradeModeratorRequest
                = new DowngradeModeratorRequest(email);

        // act and assert
        adminController.downgradeModerator(downgradeModeratorRequest);

        verify(userRoleService).downgradeModerator(email);

    }


    @Test
    void shouldDelegateUserPromotionToModeratorToUserRoleService() {

        String email = "jdoe@example.com";

        PromoteToModeratorRequest promoteToModeratorRequest
                = new PromoteToModeratorRequest(email);

        // act and assert
        adminController.promoteToModerator(promoteToModeratorRequest);

        verify(userRoleService).promoteToModerator(email);

    }
}
