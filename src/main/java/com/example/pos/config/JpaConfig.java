package com.example.pos.config;  // Use the appropriate package for your project

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.pos.repository")  // Specify the package where your repositories are
public class JpaConfig {
}
