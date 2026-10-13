package com.bookzilla.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class UserDatabaseTestConfiguration {

    @Bean("userPostgreSQLContainer")
    PostgreSQLContainer<?> userPostgreSQLContainer(
            @Value("${user.datasource.name}") String dbName
    ) {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName(dbName);
    }

    @Bean
    DynamicPropertyRegistrar userDatabaseProperties(
            @Qualifier("userPostgreSQLContainer")
            PostgreSQLContainer<?> userPostgreSQLContainer) {

        return registry -> {
            registry.add(
                    "user.datasource.url",
                    userPostgreSQLContainer::getJdbcUrl
            );
            registry.add(
                    "user.datasource.username",
                    userPostgreSQLContainer::getUsername
            );
            registry.add(
                    "user.datasource.password",
                    userPostgreSQLContainer::getPassword
            );
        };
    }
}
