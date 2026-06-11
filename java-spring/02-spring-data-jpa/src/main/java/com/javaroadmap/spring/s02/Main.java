package com.javaroadmap.spring.s02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа модуля 2 — «Spring Data JPA».
 *
 * <p>При старте Liquibase накатывает миграции на in-memory H2,
 * Hibernate валидирует, что сущности совпадают со схемой (ddl-auto=validate).
 *
 * <p>Запуск: {@code ./gradlew :java-spring:02-spring-data-jpa:run}
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
