package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserRoleService;
import com.bookzilla.auth.infrastructure.web.dto.DowngradeModeratorRequest;
import com.bookzilla.auth.infrastructure.web.dto.PromoteToModeratorRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRoleService userRoleService;

    public AdminController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @SecurityRequirement(name = "oauth2")
    @PostMapping("/downgrade-moderator")
    public void downgradeModerator(@Valid @RequestBody DowngradeModeratorRequest downgradeModeratorRequest) {
        userRoleService.downgradeModerator(downgradeModeratorRequest.email());
    }

    @SecurityRequirement(name = "oauth2")
    @PostMapping("/promote-to-moderator")
    public void promoteToModerator(@Valid @RequestBody PromoteToModeratorRequest promoteToModeratorRequest) {
        userRoleService.promoteToModerator(promoteToModeratorRequest.email());
    }

}
