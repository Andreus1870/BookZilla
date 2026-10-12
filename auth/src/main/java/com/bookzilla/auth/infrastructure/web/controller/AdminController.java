package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.DowngradeModeratorRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "oauth2")
    @PostMapping("/downgrade-moderator")
    public void downgradeModerator(@Valid @RequestBody DowngradeModeratorRequest downgradeModeratorRequest) {
        userService.downgradeModerator(downgradeModeratorRequest.email());
    }

}
