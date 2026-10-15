package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.dto.UserInfo;
import com.bookzilla.auth.application.dto.UserShortInfo;
import com.bookzilla.auth.application.service.UserAdministrationService;
import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.DeleteUserRequest;
import com.bookzilla.auth.infrastructure.web.dto.GetUserInfoRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    private final UserAdministrationService userAdministrationService;
    private final UserService userService;

    public ModeratorController(UserAdministrationService userAdministrationService, UserService userService) {
        this.userAdministrationService = userAdministrationService;
        this.userService = userService;
    }

    @SecurityRequirements({
            @SecurityRequirement(name = "oauth2"),
            @SecurityRequirement(name = "bearerAuth")
    })
    @PostMapping("/delete-user")
    public void deleteUser(@Valid @RequestBody DeleteUserRequest deleteUserRequest){
        userAdministrationService.deleteUser(deleteUserRequest.email());
    }

    @SecurityRequirements({
            @SecurityRequirement(name = "oauth2"),
            @SecurityRequirement(name = "bearerAuth")
    })
    @GetMapping("/get-all-users")
    public List<UserShortInfo> getAllUsers() {
        return userAdministrationService.getAllUsers();
    }

    @SecurityRequirements({
            @SecurityRequirement(name = "oauth2"),
            @SecurityRequirement(name = "bearerAuth")
    })
    @GetMapping("/get-user-info")
    public UserInfo getUserInfo(@Valid @ModelAttribute GetUserInfoRequest userInfoRequest){
        return userService.getUserInfoByEmail(userInfoRequest.email());
    }
}
