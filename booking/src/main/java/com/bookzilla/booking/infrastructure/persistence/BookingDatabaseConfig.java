package com.bookzilla.booking.infrastructure.persistence;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.bookzilla.booking.infrastructure.persistence",
        entityManagerFactoryRef = "bookingEntityManagerFactory",
        transactionManagerRef = "bookingTransactionManager"
)
public class BookingDatabaseConfig {

    @Bean
    @ConfigurationProperties("booking.datasource")
    public DataSourceProperties bookingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource bookingDataSource(
            @Qualifier("bookingDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(initMethod = "migrate")
    public Flyway bookingFlyway(
            @Qualifier("bookingDataSource") DataSource dataSource) {

        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/booking")
                .load();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean bookingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bookingDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.bookzilla.booking.domain")
                .persistenceUnit("booking")
                .build();
    }

    @Bean
    public PlatformTransactionManager bookingTransactionManager(
            @Qualifier("bookingEntityManagerFactory")
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}