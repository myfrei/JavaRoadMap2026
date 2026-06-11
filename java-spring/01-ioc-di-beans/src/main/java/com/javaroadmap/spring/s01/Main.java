package com.javaroadmap.spring.s01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа модуля 1 — «IoC, DI и бины».
 *
 * <p>Запуск: {@code ./gradlew :java-spring:01-ioc-di-beans:run}
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
