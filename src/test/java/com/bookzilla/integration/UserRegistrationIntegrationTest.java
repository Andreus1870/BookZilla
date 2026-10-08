package com.bookzilla.integration;

import com.bookzilla.integration.config.PostgresDatabaseConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresDatabaseConfiguration.class)
public class UserRegistrationIntegrationTest {

    @Test
    void contextLoads() {
    }


}
