package com.bookzilla.auth.infrastructure.web.dto;

import java.time.Instant;

public record UserShortInfo(

        String email,

        String firstName,

        String lastName,

        Instant registrationDate
) {
}
