package com.bookzilla.auth.infrastructure.web.controller;

import com.bookzilla.auth.application.service.UserService;
import com.bookzilla.auth.infrastructure.web.dto.DeleteUserRequest;
import com.bookzilla.auth.infrastructure.web.dto.UserShortInfo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    private final UserService userService;

    public ModeratorController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "oauth2")
    @PostMapping("/delete-user")
    public void deleteUser(@Valid @RequestBody DeleteUserRequest deleteUserRequest){
        userService.deleteUser(deleteUserRequest.email());
    }

    @SecurityRequirement(name = "oauth2")
    @GetMapping("/get-all-users")
    public List<UserShortInfo> getAllUsers(){
        return userService.getAllUsers();
    }

}
