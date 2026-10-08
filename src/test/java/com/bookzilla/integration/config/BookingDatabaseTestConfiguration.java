package com.bookzilla.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class BookingDatabaseTestConfiguration {

    @Bean("bookingPostgreSQLContainer")
    PostgreSQLContainer<?> bookingPostgreSQLContainer(
            @Value("${booking.datasource.name}") String dbName
    ) {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName(dbName);
    }

    @Bean
    DynamicPropertyRegistrar bookingDatabaseProperties(
            @Qualifier("bookingPostgreSQLContainer")
            PostgreSQLContainer<?> bookingPostgreSQLContainer) {

        return registry -> {
            registry.add(
                    "booking.datasource.url",
                    bookingPostgreSQLContainer::getJdbcUrl
            );
            registry.add(
                    "booking.datasource.username",
                    bookingPostgreSQLContainer::getUsername
            );
            registry.add(
                    "booking.datasource.password",
                    bookingPostgreSQLContainer::getPassword
            );
        };
    }
}
