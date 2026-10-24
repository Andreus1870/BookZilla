package com.bookzilla.auth.domain.operation.payload;

import java.util.UUID;

public record DeleteKeycloakUserPayload(
        UUID keycloakId
) {
}
