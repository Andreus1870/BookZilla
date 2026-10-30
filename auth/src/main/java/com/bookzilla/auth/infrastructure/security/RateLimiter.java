package com.bookzilla.auth.infrastructure.security;

import java.util.UUID;

@FunctionalInterface
public interface RateLimiter {

    public boolean isAllowed(UUID userId);

}
