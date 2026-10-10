package com.bookzilla.auth.application.dto;

import java.time.Instant;

public record UserShortInfo(

        String email,

        String firstName,

        String lastName,

        Instant registrationDate
) {
}
