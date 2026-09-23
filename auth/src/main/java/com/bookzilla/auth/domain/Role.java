package com.bookzilla.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN;

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }

    public static Role fromAuthority(GrantedAuthority authority) {
        return valueOf(
                authority.getAuthority().substring("ROLE_".length())
        );
    }
}
