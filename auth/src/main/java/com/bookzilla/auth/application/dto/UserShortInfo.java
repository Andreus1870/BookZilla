package com.bookzilla.auth.application.dto;

import com.bookzilla.auth.domain.user.BusinessRole;

import java.time.Instant;

public record UserShortInfo(

        String email,

        String firstName,

        String lastName,

        Instant registrationDate,

        BusinessRole role
) {
}
