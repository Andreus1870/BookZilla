package com.bookzilla.integration;

import com.bookzilla.auth.application.port.out.IdentityProvider;
import com.bookzilla.auth.domain.User;
import com.bookzilla.auth.infrastructure.persistence.JpaUserRepository;
import com.bookzilla.booking.domain.Client;
import com.bookzilla.booking.domain.Provider;
import com.bookzilla.booking.infrastructure.persistence.client.JpaClientRepository;
import com.bookzilla.booking.infrastructure.persistence.provider.JpaProviderRepository;
import com.bookzilla.integration.config.BookingDatabaseTestConfiguration;
import com.bookzilla.integration.config.UserDatabaseTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Import({
        BookingDatabaseTestConfiguration.class,
        UserDatabaseTestConfiguration.class
})
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaProviderRepository jpaProviderRepository;

    @Autowired
    private JpaClientRepository jpaClientRepository;

    @MockitoBean
    private IdentityProvider identityProvider;


    @BeforeEach
    void cleanUp() {
        jpaClientRepository.deleteAll();
        jpaProviderRepository.deleteAll();
        jpaUserRepository.deleteAll();
    }


    @Test
    void shouldRegisterUser() throws Exception {

        // arrange
        UUID keycloakUserId = UUID.randomUUID();

        when(identityProvider.createIdentity(
                "Andrii",
                "Test",
                "andrii@test.com",
                "password123"
        )).thenReturn(keycloakUserId);


        // act and assert
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                 {
                                    "firstName": "Andrii",
                                    "lastName": "Test",
                                    "email": "andrii@test.com",
                                    "password": "password123"
                                 }
                                 """)
                )
                .andExpect(status().isOk());

        verify(identityProvider).createIdentity(
                "Andrii",
                "Test",
                "andrii@test.com",
                "password123"
        );

        User user = jpaUserRepository
                .findByEmailIgnoreCase("andrii@test.com")
                .orElseThrow();

        assertEquals("Andrii", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertEquals("andrii@test.com", user.getEmail());
        assertEquals(keycloakUserId, user.getKeycloakId());

        Provider provider = jpaProviderRepository
                .findById(user.getId())
                .orElseThrow();

        Client client = jpaClientRepository
                .findById(user.getId())
                .orElseThrow();

        assertEquals(user.getId(), provider.getId());
        assertEquals(user.getId(), client.getId());
    }



    @Test
    void shouldNotRegisterUserWhenEmailAlreadyExists() throws Exception {

        //arrange
        UUID keycloakUserId = UUID.randomUUID();

        when(identityProvider.createIdentity(
                "Andrii",
                "Test2",
                "test2@test.com",
                "test123456"
        )).thenReturn(keycloakUserId);

        // act and assert
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""
                                 {
                                    "firstName": "Andrii",
                                    "lastName": "Test2",
                                    "email": "test2@test.com",
                                    "password": "test123456"
                                 }
                                 """)
                )
                .andExpect(status().isOk());


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType("application/json")
                                .content("""

                                        {
                                    "firstName": "Andrii",
                                    "lastName": "Test2",
                                    "email": "test2@test.com",
                                    "password": "test123456"
                                 }
                                 """)
                )
                .andExpect(status().isConflict());

        verify(identityProvider).createIdentity(
                "Andrii",
                "Test2",
                "test2@test.com",
                "test123456"
        );

        assertEquals(1, jpaClientRepository.count());
        assertEquals(1, jpaProviderRepository.count());
        assertEquals(1, jpaUserRepository.count());
    }

}
