package com.javaroadmap.spring.s04;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Точка входа модуля 4 — «Конфигурация и безопасность».
 *
 * <p>@ConfigurationPropertiesScan находит все @ConfigurationProperties-классы
 * пакета — регистрировать каждый вручную не нужно.
 *
 * <p>Запуск: {@code ./gradlew :java-spring:04-config-security:run}
 * Пользователи: user/user123 (USER), admin/admin123 (ADMIN).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
