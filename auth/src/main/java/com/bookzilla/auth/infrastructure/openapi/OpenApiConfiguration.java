package com.bookzilla.auth.infrastructure.openapi;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
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
)
public class OpenApiConfiguration {

}
