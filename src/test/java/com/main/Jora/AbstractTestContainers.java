package com.main.Jora;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestContainers {
    protected static MySQLContainer<?> mySQLContainer;

    public static void init() {
        if (mySQLContainer != null && mySQLContainer.isRunning()) return;

        String profile = System.getProperty("spring.profiles.active", "test");

        String dbName = switch (profile) {
            case "test" -> "joratestdb";
            case "test-ui" -> "uitestdb";
            default -> throw new RuntimeException("Unknown profile: " + profile);
        };
        System.out.println("Using profile: " + profile);
        System.out.println("DB name: " + dbName);

        mySQLContainer = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName(dbName)
                .withUsername("test")
                .withPassword("test");
        mySQLContainer.start();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                mySQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                mySQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                mySQLContainer::getPassword
        );
    }
    protected static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(mySQLContainer.getDriverClassName())
                .url(mySQLContainer.getJdbcUrl())
                .username(mySQLContainer.getUsername())
                .password(mySQLContainer.getPassword())
                .build();
    }
    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}