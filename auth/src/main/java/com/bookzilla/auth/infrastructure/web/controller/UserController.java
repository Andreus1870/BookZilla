package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.UserInfo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "oauth2")
    @GetMapping("/me")
    public UserInfo getUserInfo(Authentication authentication){
        UUID keycloakUserId = UUID.fromString(authentication.getName());

        return userService.getUserInfoByKeycloakId(keycloakUserId);
    }
}
