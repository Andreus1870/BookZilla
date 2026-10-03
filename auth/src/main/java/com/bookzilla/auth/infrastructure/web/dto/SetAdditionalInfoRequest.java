package com.bookzilla.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public record SetAdditionalInfoRequest(

        @Size(max = 32)
        String phone,

        @Size(max = 100)
        String country,

        @Size(max = 100)
        String city
) {
}
