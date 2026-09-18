package com.bookzilla.auth.infrastructure.web.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        @NotBlank
        @Email
        @Size(max = 254)
        String email
) {
}
