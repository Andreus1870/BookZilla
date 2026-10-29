package com.bookzilla.auth.infrastructure.persistence.database.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
public class CompensationDatabaseConfig {

    @Bean
    @ConfigurationProperties("compensation.datasource")
    public DataSourceProperties compensationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource compensationDataSource(
            @Qualifier("compensationDataSourceProperties") DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(initMethod = "migrate")
    public Flyway compensationFlyway(
            @Qualifier("compensationDataSource") DataSource dataSource) {

        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/compensation")
                .load();
    }

    @Bean
    public JdbcClient compensationJdbcClient(
            @Qualifier("compensationDataSource") DataSource dataSource
    ) {
        return JdbcClient.create(dataSource);
    }

}