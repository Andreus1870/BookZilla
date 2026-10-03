package com.bookzilla.auth.infrastructure.identity;

import com.bookzilla.auth.application.exception.KeycloakUserCreationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeycloakIdentityProviderTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleRepresentation roleRepresentation;

    @Mock
    private UserResource userResource;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @Mock
    private Response response;


    private static final String KEYCLOAK_REALM = "bookzilla";

    private static final String KEYCLOAK_USER_URL =
            "http://localhost:8081/admin/realms/bookzilla/users/";


    private KeycloakIdentityProvider keycloakIdentityProvider;

    @BeforeEach
    void setUp() {
        keycloakIdentityProvider =
                new KeycloakIdentityProvider(
                        keycloak, KEYCLOAK_REALM
                );
    }

    @Test
    void shouldCreateIdentitySuccessfully(){
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String password = "123456789";

        UUID keycloakId = UUID.randomUUID();

        when(keycloak.realm(KEYCLOAK_REALM))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.create(any(UserRepresentation.class)))
                .thenReturn(response);

        when(response.getStatusInfo())
                .thenReturn(Response.Status.CREATED);

        when(response.getStatus()).thenReturn(201);

        when(response.getLocation()).thenReturn(
                URI.create(
                        KEYCLOAK_USER_URL + keycloakId
                )
        );

        when(realmResource.roles())
                .thenReturn(rolesResource);

        when(rolesResource.get("USER"))
                .thenReturn(roleResource);

        when(roleResource.toRepresentation())
                .thenReturn(roleRepresentation);

        when(usersResource.get(keycloakId.toString()))
                .thenReturn(userResource);

        when(userResource.roles())
                .thenReturn(roleMappingResource);

        when(roleMappingResource.realmLevel())
                .thenReturn(roleScopeResource);


        // act
        UUID result = keycloakIdentityProvider.createIdentity(
                firstName,
                lastName,
                email,
                password
        );


        // assert
        assertThat(result).isEqualTo(keycloakId);

        verify(response).getStatus();
        verify(response).getLocation();

        verify(realmResource).roles();
        verify(rolesResource).get("USER");
        verify(roleResource).toRepresentation();

        verify(usersResource).get(keycloakId.toString());
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();

        verify(roleScopeResource).add(List.of(roleRepresentation));

        ArgumentCaptor<UserRepresentation> userCaptor =
                ArgumentCaptor.forClass(UserRepresentation.class);

        verify(usersResource).create(userCaptor.capture());

        UserRepresentation createdUser = userCaptor.getValue();

        assertThat(createdUser.getUsername()).isEqualTo(email);
        assertThat(createdUser.getFirstName()).isEqualTo(firstName);
        assertThat(createdUser.getLastName()).isEqualTo(lastName);
        assertThat(createdUser.getEmail()).isEqualTo(email);
        assertThat(createdUser.isEnabled()).isTrue();

        CredentialRepresentation credential =
                createdUser.getCredentials().getFirst();

        assertThat(credential.getType())
                .isEqualTo(CredentialRepresentation.PASSWORD);
        assertThat(credential.getValue())
                .isEqualTo(password);
        assertThat(credential.isTemporary())
                .isFalse();

    }


    @Test
    void shouldThrowExceptionWhenKeycloakUserCreationFails() {
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String password = "123456789";

        when(keycloak.realm(KEYCLOAK_REALM))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.create(any(UserRepresentation.class)))
                .thenReturn(response);

        when(response.getStatus()).thenReturn(401);


        //act and assert
        assertThrows(
            KeycloakUserCreationException.class,
                () -> keycloakIdentityProvider.createIdentity(
                        firstName,
                        lastName,
                        email,
                        password
                )
        );
        verify(realmResource, never()).roles();
    }


    @Test
    void shouldThrowExceptionWhenCreatedUserIdIsMissing() {
        // arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String password = "123456789";


        when(keycloak.realm(KEYCLOAK_REALM))
                .thenReturn(realmResource);

        when(realmResource.users())
                .thenReturn(usersResource);

        when(usersResource.create(any(UserRepresentation.class)))
                .thenReturn(response);

        when(response.getStatusInfo())
                .thenReturn(Response.Status.CREATED);

        when(response.getStatus()).thenReturn(201);

        when(response.getLocation()).thenReturn(null);


        //act and assert
        KeycloakUserCreationException exception = assertThrows(
                KeycloakUserCreationException.class,
                () -> keycloakIdentityProvider.createIdentity(
                        firstName,
                        lastName,
                        email,
                        password
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Keycloak did not return an ID for the created user");
    }
}
