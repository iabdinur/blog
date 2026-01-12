package com.iabdinur;

import com.github.javafaker.Faker;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestcontainers {

    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;
    protected static final Faker FAKER = new Faker();

    @Container
    protected static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("blog-test")
                    .withUsername("iabdinur")
                    .withPassword("password");

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword()
                ).load();
        flyway.migrate();

        // Initialize the singleton datasource
        dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterAll
    static void afterAll() {
        // Close datasource if it's a HikariDataSource
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    private static DataSource createDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        hikariDataSource.setUsername(postgreSQLContainer.getUsername());
        hikariDataSource.setPassword(postgreSQLContainer.getPassword());
        hikariDataSource.setDriverClassName(postgreSQLContainer.getDriverClassName());

        // Match the exact same config as in your application.yml
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setMinimumIdle(2);
        hikariDataSource.setIdleTimeout(6000);
        hikariDataSource.setMaxLifetime(180000);
        hikariDataSource.setConnectionTimeout(30000);

        return hikariDataSource;
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
