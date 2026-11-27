package com.bookzilla.auth.infrastructure.security;

import java.util.UUID;

@FunctionalInterface
public interface RateLimiter {

    boolean isAllowed(UUID userId);

}
