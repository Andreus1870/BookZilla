package com.bookzilla.auth.application.service;

import com.bookzilla.auth.domain.Role;
import com.bookzilla.auth.infrastructure.token.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class AccessTokenService {

    private final JwtProperties jwtProperties;

    public AccessTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(UUID userId, Role role) {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(jwtProperties.getSecret())
        );

        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                   .subject(userId.toString())
                   .claim("role", role.name())
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(expiration))
                   .signWith(key)
                   .compact();
    }
}
