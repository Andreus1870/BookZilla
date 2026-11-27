package com.bookzilla.integration.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class CompensationDatabaseTestConfiguration {

    @Bean("compensationPostgreSQLContainer")
    PostgreSQLContainer<?> compensationPostgreSQLContainer(
            @Value("${compensation.datasource.name}") String dbName
    ) {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName(dbName);
    }

    @Bean
    DynamicPropertyRegistrar compensationDatabaseProperties(
            @Qualifier("compensationPostgreSQLContainer")
            PostgreSQLContainer<?> compensationPostgreSQLContainer) {

        return registry -> {
            registry.add(
                    "compensation.datasource.url",
                    compensationPostgreSQLContainer::getJdbcUrl
            );
            registry.add(
                    "compensation.datasource.username",
                    compensationPostgreSQLContainer::getUsername
            );
            registry.add(
                    "compensation.datasource.password",
                    compensationPostgreSQLContainer::getPassword
            );
        };
    }
}
