package com.bookzilla.auth.infrastructure.openapi;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecuritySchemes({
        @SecurityScheme(
                name = "oauth2",
                type = SecuritySchemeType.OAUTH2,
                flows = @OAuthFlows(
                        authorizationCode = @OAuthFlow(
                                authorizationUrl = "http://localhost:8081/realms/bookzilla/protocol/openid-connect/auth",
                                tokenUrl = "http://localhost:8081/realms/bookzilla/protocol/openid-connect/token",
                                scopes = {
                                        @OAuthScope(
                                                name = "openid",
                                                description = "OpenID Connect"
                                        )
                                }
                        )
                )
        ),
        @SecurityScheme(
                name = "bearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        )
})
public class OpenApiConfiguration {
}
