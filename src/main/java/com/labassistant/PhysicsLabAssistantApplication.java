package com.labassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.labassistant.repository")
@EntityScan(basePackages = "com.labassistant.model")
public class PhysicsLabAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhysicsLabAssistantApplication.class, args);
    }
}