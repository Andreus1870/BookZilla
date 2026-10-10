package com.bookzilla.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;

public record GetUserInfoRequest(
        @Email
        String email
) {
}
