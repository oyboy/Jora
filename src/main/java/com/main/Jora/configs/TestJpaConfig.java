package com.main.Jora.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.main.Jora.repositories")
@EntityScan(basePackages = "com.main.Jora.models")
public class TestJpaConfig {
}