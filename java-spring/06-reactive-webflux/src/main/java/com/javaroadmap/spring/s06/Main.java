package com.javaroadmap.spring.s06;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа модуля 6 — «Реактивный стек».
 *
 * <p>WebFlux на Netty (не Tomcat!), хранилище — реактивный H2 через R2DBC.
 * Запуск: {@code ./gradlew :java-spring:06-reactive-webflux:run}
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
