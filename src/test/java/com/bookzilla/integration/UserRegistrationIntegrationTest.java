package com.bookzilla.integration;

import com.bookzilla.integration.config.BookingDatabaseTestConfiguration;
import com.bookzilla.integration.config.UserDatabaseTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({
        BookingDatabaseTestConfiguration.class,
        UserDatabaseTestConfiguration.class
})
public class UserRegistrationIntegrationTest {

    @Test
    void contextLoads() {
    }


}
